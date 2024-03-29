package maquette.development.entities;

import akka.Done;
import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.ports.mlprojects.MLProjectCreationPort;
import maquette.development.ports.models.ModelServingPort;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.exceptions.WorkspaceAlreadyExistsException;
import maquette.development.values.exceptions.WorkspaceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Container for operations across all/ multiple workspaces.
 */
@AllArgsConstructor(staticName = "apply")
public final class WorkspaceEntities { // implements maquette.workspaces.api.WorkspaceEntities {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceEntities.class);

    /**
     * Repository to store/ read workspace information.
     */
    private final WorkspacesRepository repository;

    /**
     * Repository to store/ read model information.
     */
    private final ModelsRepository models;

    /**
     * Port to instantiate infrastructure for a workspace.
     */
    private final InfrastructurePort infrastructurePort;

    /**
     * Port to instantiate new services.
     */
    private final ModelServingPort modelServing;

    private final MLProjectCreationPort mlProjects;

    /**
     * Creates a new workspace if it does not exist.
     *
     * @param executor The user who executes the action.
     * @param name     The name of the workspace.
     * @param title    The title of the workspace.
     * @param summary  The summary/ short description of the workspace' purpose.
     * @return Properties of the created workspace.
     */
    public CompletionStage<WorkspaceProperties> createWorkspace(User executor,
                                                                String name,
                                                                String title,
                                                                String summary) {
        return findWorkspaceByName(name)
            .thenCompose(maybeWorkspace -> {
                if (maybeWorkspace.isPresent()) {
                    return maybeWorkspace
                        .get()
                        .isMember(executor, WorkspaceMemberRole.ADMIN)
                        .thenCompose(result -> {
                            if (result) {
                                return maybeWorkspace
                                    .get()
                                    .getProperties();
                            } else {
                                return CompletableFuture.failedFuture(WorkspaceAlreadyExistsException.apply(name));
                            }
                        });
                } else {
                    var created = ActionMetadata.apply(executor, Instant.now());
                    var properties = WorkspaceProperties.apply(UID.apply(), name, title, summary, created, created);

                    return repository
                        .insertOrUpdateWorkspace(properties)
                        .thenApply(done -> properties);
                }
            });
    }

    /**
     * Use this function to trigger refresh of all model information from MLflow instances.
     * <p>
     * This is a long running-process and should be triggered in background.
     *
     * @return Done.
     */
    public CompletionStage<Done> refreshModelInformationFromMlflow() {
        var pool = Executors.newFixedThreadPool(5);

        return repository
            .findAllWorkspaces()
            .thenCompose(workspaces -> {
                var updates = workspaces.map(workspaceProperties -> {
                    var workspace = WorkspaceEntity.apply(
                        workspaceProperties.getId(), repository, models, modelServing, mlProjects, infrastructurePort
                    );

                    return (CompletionStage<Done>) CompletableFuture
                        .supplyAsync(
                            () -> {
                                LOG.trace("Updating models for {}", workspaceProperties.getName());

                                return Operators.suppressExceptions(() -> workspace
                                    .getModels()
                                    .thenApply(ModelEntities::getModels)
                                    .toCompletableFuture()
                                    .get());
                            },
                            pool
                        )
                        .thenApply(i -> Done.getInstance())
                        .exceptionally(ex -> {
                            LOG.warn(MessageFormat.format(
                                "Exception occurred updating models for workspace `{0}`",
                                workspaceProperties.getName()
                            ));

                            return Done.getInstance();
                        });
                });

                return Operators.allOf(updates);
            })
            .thenApply(all -> {
                pool.shutdown();
                return Done.getInstance();
            });
    }

    /**
     * Find a workspace by its unique id.
     *
     * @param id The unique id of the workspace.
     * @return Workspace if found.
     */
    public CompletionStage<Optional<WorkspaceEntity>> findWorkspaceById(UID id) {
        return repository
            .findWorkspaceById(id)
            .thenApply(maybeWorkspace -> maybeWorkspace.map(project ->
                WorkspaceEntity.apply(project.getId(), repository, models, modelServing, mlProjects,
                    infrastructurePort)));
    }

    /**
     * Find a workspace by its unique name.
     *
     * @param name The name of the workspace.
     * @return Workspace if found.
     */
    public CompletionStage<Optional<WorkspaceEntity>> findWorkspaceByName(String name) {
        return repository
            .findWorkspaceByName(name)
            .thenApply(maybeWorkspace -> maybeWorkspace.map(project ->
                WorkspaceEntity.apply(project.getId(), repository, models, modelServing, mlProjects,
                    infrastructurePort)));
    }

    /**
     * Find a workspace by its MLflow id. The MLflow id is derived from the workspace ID and thus is also unique.
     *
     * @param mlflowId The MLflow id of the workspace.
     * @return The workspace entity, of found.
     */
    public CompletionStage<Optional<WorkspaceEntity>> findWorkspaceByMlflowId(String mlflowId) {
        return repository
            .findWorkspaceById(UID.apply(mlflowId.replace(WorkspaceEntity.MLFLOW_INSTANCE_PREFIX, "")))
            .thenApply(maybeProperties -> maybeProperties.map(properties ->
                WorkspaceEntity.apply(
                    properties.getId(), repository, models, modelServing, mlProjects, infrastructurePort
                )));
    }

    /**
     * Same as {@link WorkspaceEntities#findWorkspaceByMlflowId(String)}.
     *
     * @param mlflowId The MLflow id of the workspace.
     * @return The workspace entity.
     * @throws WorkspaceNotFoundException if workspace is not found.
     */
    public CompletionStage<WorkspaceEntity> getWorkspaceByMlflowId(String mlflowId) {
        return this.findWorkspaceByMlflowId(mlflowId)
            .thenApply(maybeWorkspace -> maybeWorkspace.orElseThrow(() -> WorkspaceNotFoundException.applyFromName(mlflowId)));
    }

    /**
     * Like {@link WorkspaceEntities#findWorkspaceById(UID)}. But throws an exception if workspace not found.
     *
     * @param id The unique id of the workspace.
     * @return The workspace.
     * @throws WorkspaceNotFoundException if workspace is not found.
     */
    public CompletionStage<WorkspaceEntity> getWorkspaceById(UID id) {
        return findWorkspaceById(id)
            .thenApply(opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromId(id)));
    }

    /**
     * Like {@link WorkspaceEntities#findWorkspaceByName(String)}. But throws an exception if workspace not found.
     *
     * @param name The unique name of the workspace.
     * @return The workspace.
     * @throws WorkspaceNotFoundException if workspace is not found.
     */
    public CompletionStage<WorkspaceEntity> getWorkspaceByName(String name) {
        return findWorkspaceByName(name)
            .thenApply(opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromName(name)));
    }

    /**
     * Get a list of all workspaces.
     *
     * @return Properties of all existing workspaces.
     */
    public CompletionStage<List<WorkspaceProperties>> getWorkspaces() {
        return repository
            .getWorkspaces()
            .thenApply(s -> s.collect(Collectors.toList()));
    }

    /**
     * Get all workspaces a user has access to.
     *
     * @param user The user requesting the list.
     * @return The list of available workspaces.
     */
    public CompletionStage<List<WorkspaceProperties>> getWorkspacesByMember(User user) {
        return repository
            .getWorkspaces()
            .thenCompose(all -> Operators.allOf(all
                .map(p -> WorkspaceEntity
                    .apply(p.getId(), repository, models, modelServing, mlProjects, infrastructurePort)
                    .members()
                    .getMembers()
                    .thenApply(members -> Pair.create(p, members)))))
            .thenApply(all -> all
                .stream()
                .filter(p -> {
                    var members = p.second();
                    return members
                        .stream()
                        .anyMatch(granted -> granted
                            .getAuthorization()
                            .authorizes(user));
                })
                .map(Pair::first)
                .collect(Collectors.toList()));
    }

    /**
     * Removes a workspace (metadata) from the database.
     *
     * @param id The unique id of the workspace.
     * @return Done.
     */
    public CompletionStage<Done> removeWorkspace(UID id) {
        var removeInfraCS = infrastructurePort.removeStackInstance(WorkspaceEntity.getMlflowStackName(id));
        var removeMetadataCS = repository.removeWorkspace(id);

        return Operators.compose(removeInfraCS, removeMetadataCS, (i, m) -> Done.getInstance());
    }

    /**
     * Checks the current state of the infrastructure and redeploys infrastructure if necessary.
     *
     * @return Done (Request has been accepted, but actual deployment might take longer).
     */
    public CompletionStage<Done> redeployInfrastructure() {
        return infrastructurePort.checkState(false);
    }

}
