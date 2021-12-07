package maquette.development.entities;

import akka.Done;
import akka.japi.Pair;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.ports.InfrastructurePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.exceptions.WorkspaceAlreadyExistsException;
import maquette.development.values.exceptions.WorkspaceNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class WorkspaceEntities { // implements maquette.workspaces.api.WorkspaceEntities {

    private final WorkspacesRepository repository;

    private final ModelsRepository models;

    private final InfrastructurePort infrastructurePort;

    public CompletionStage<WorkspaceProperties> createWorkspace(User executor,
                                                                String name,
                                                                String title,
                                                                String summary) {
        return findWorkspaceByName(name)
            .thenCompose(maybeWorkspace -> {
                if (maybeWorkspace.isPresent()) {
                    return maybeWorkspace.get()
                        .isMember(executor, WorkspaceMemberRole.ADMIN)
                        .thenCompose(result -> {
                            if (result) {
                                return maybeWorkspace.get().getProperties();
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

    public CompletionStage<Optional<WorkspaceEntity>> findWorkspaceById(UID id) {
        return repository
            .findWorkspaceById(id)
            .thenApply(maybeWorkspace -> maybeWorkspace.map(project ->
                WorkspaceEntity.apply(project.getId(), repository, models, infrastructurePort)));
    }

    public CompletionStage<Optional<WorkspaceEntity>> findWorkspaceByName(String name) {
        return repository
            .findWorkspaceByName(name)
            .thenApply(maybeWorkspace -> maybeWorkspace.map(project ->
                WorkspaceEntity.apply(project.getId(), repository, models, infrastructurePort)));
    }

    public CompletionStage<WorkspaceEntity> getWorkspaceById(UID id) {
        return findWorkspaceById(id)
            .thenApply(opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromId(id)));
    }

    public CompletionStage<WorkspaceEntity> getWorkspaceByName(String name) {
        return findWorkspaceByName(name)
            .thenApply(opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromName(name)));
    }

    public CompletionStage<List<WorkspaceProperties>> getWorkspaces() {
        return repository.getWorkspaces().thenApply(s -> s.collect(Collectors.toList()));
    }

    public CompletionStage<List<WorkspaceProperties>> getWorkspacesByMember(User user) {
        return repository
            .getWorkspaces()
            .thenCompose(all -> Operators.allOf(all
                .map(p -> WorkspaceEntity
                    .apply(p.getId(), repository, models, infrastructurePort)
                    .members()
                    .getMembers()
                    .thenApply(members -> Pair.create(p, members)))))
            .thenApply(all -> all
                .stream()
                .filter(p -> {
                    var members = p.second();
                    return members.stream().anyMatch(granted -> granted.getAuthorization().authorizes(user));
                })
                .map(Pair::first)
                .collect(Collectors.toList()));
    }

    public CompletionStage<Done> removeWorkspace(UID id) {
        var removeInfraCS = infrastructurePort.removeStackInstance(WorkspaceEntity.getMlflowStackName(id));
        var removeMetadataCS = repository.removeWorkspace(id);

        return Operators.compose(removeInfraCS, removeMetadataCS, (i, m) -> Done.getInstance());
    }

}
