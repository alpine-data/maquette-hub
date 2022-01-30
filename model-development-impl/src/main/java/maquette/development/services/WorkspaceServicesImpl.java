package maquette.development.services;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.development.entities.*;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.values.EnvironmentType;
import maquette.development.values.Workspace;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelMembersCompanion;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.events.Approved;
import maquette.development.values.model.events.Rejected;
import maquette.development.values.model.events.ReviewRequested;
import maquette.development.values.model.governance.CodeIssue;
import maquette.development.values.model.governance.CodeQuality;
import maquette.development.values.sandboxes.Sandbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkspaceServicesImpl implements WorkspaceServices {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceServices.class);

    WorkspaceEntities workspaces;

    SandboxEntities sandboxes;

    DataAssetsServicePort dataAssets;

    public static WorkspaceServicesImpl apply(
        WorkspaceEntities projects, SandboxEntities sandboxes, DataAssetsServicePort dataAssets) {

        return new WorkspaceServicesImpl(projects, sandboxes, dataAssets);
    }

    @Override
    public CompletionStage<Done> create(User executor, String name, String title, String summary) {
        return workspaces
            .createWorkspace(executor, name, title, summary)
            .thenCompose(project -> workspaces.getWorkspaceById(project.getId()))
            .thenCompose(workspace -> {
                var adminAddedCS = workspace.members()
                    .addMember(executor, executor.toAuthorization(), WorkspaceMemberRole.ADMIN);

                var mlFlowInitializedCS = workspace.initializeMlflowEnvironment();

                return Operators.compose(
                    adminAddedCS, mlFlowInitializedCS,
                    (adminAdded, mlFlowInitialized) -> Done.getInstance());
            });
    }

    @Override
    public CompletionStage<Map<String, String>> environment(User user, String workspace, EnvironmentType type) {
        return workspaces.getWorkspaceByName(workspace).thenCompose(WorkspaceEntity::getEnvironment);
    }

    @Override
    public CompletionStage<List<WorkspaceProperties>> list(User user) {
        return workspaces.getWorkspaces();
    }

    @Override
    public CompletionStage<Workspace> get(User user, String name) {
        return workspaces
            .getWorkspaceByName(name)
            .thenCompose(workspace -> {
                var propertiesCS = workspace.getProperties();
                var membersCS = workspace.members().getMembers();
                var accessRequestsCS = dataAssets.findDataAccessRequestsByWorkspace(workspace.getId());
                var dataAssetsCS = dataAssets.findDataAssetsByWorkspace(workspace.getId());
                var sandboxesCS = sandboxes
                    .listSandboxes(workspace.getId())
                    .thenCompose(sdbxProperties -> Operators.allOf(
                        sdbxProperties
                            .stream()
                            .map(properties -> sandboxes
                                .getSandboxById(workspace.getId(), properties.getId())
                                .thenCompose(SandboxEntity::getState)
                                .thenApply(stacks -> Sandbox.apply(properties, stacks)))));

                return Operators.compose(
                    propertiesCS, accessRequestsCS, membersCS, dataAssetsCS, sandboxesCS,
                    Workspace::apply);
            });
    }

    @Override
    public CompletionStage<Done> remove(User user, String workspace) {
        return workspaces
            .findWorkspaceByName(workspace)
            .thenCompose(maybeWorkspace -> {
                if (maybeWorkspace.isPresent()) {
                    var workspaceId = maybeWorkspace.get().getId();

                    var removeSandboxes = sandboxes.removeSandboxes(workspaceId);
                    var removeWorkspace = workspaces.removeWorkspace(workspaceId);

                    return Operators.compose(removeSandboxes, removeWorkspace, (r1, r2) -> Done.getInstance());
                } else {
                    return CompletableFuture.completedFuture(Done.getInstance());
                }
            });
    }

    @Override
    public CompletionStage<Done> update(User user, String workspace, String updatedName, String title, String summary) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(project -> project.updateProperties(user, updatedName, title, summary));
    }

    /*
     * Model management
     */

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String workspace) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(WorkspaceEntity::getModels)
            .thenCompose(ModelEntities::getModels);
    }

    @Override
    public CompletionStage<Model> getModel(User user, String project, String model) {
        var projectCS = workspaces
            .getWorkspaceByName(project);

        var modelEntityCS = projectCS
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model));

        var projectMembersCS = projectCS
            .thenCompose(p -> p.members().getMembers());

        return Operators
            .compose(modelEntityCS, projectMembersCS, (modelEntity, projectMembers) -> {
                var propertiesCS = modelEntity.getProperties();
                var membersCS = modelEntity.getMembers();
                var permissionsCS = membersCS
                    .thenApply(members -> ModelMembersCompanion.apply(members, projectMembers))
                    .thenApply(comp -> comp.getDataAssetPermissions(user));

                return Operators.compose(propertiesCS, membersCS, permissionsCS, Model::fromProperties);
            })
            .thenCompose(cs -> cs);
    }

    @Override
    public CompletionStage<Done> updateModel(User user, String project, String model, String title,
                                             String description) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModel(user, title, description));
    }

    @Override
    public CompletionStage<Done> updateModelVersion(User user, String project, String model, String version,
                                                    String description) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModelVersion(user, version, mdl -> mdl.withDescription(description)));
    }

    @Override
    public CompletionStage<Done> answerQuestionnaire(User user, String project, String model, String version,
                                                     JsonNode responses) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.answerQuestionnaire(user, version, responses));
    }

    @Override
    public CompletionStage<Done> approveModel(User user, String project, String model, String version) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModelVersion(
                user, version,
                v -> v.withEvent(Approved.apply(ActionMetadata.apply(user)))));
    }

    @Override
    public CompletionStage<Done> promoteModel(User user, String project, String model, String version, String stage) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.promoteModel(user, version, stage));
    }

    @Override
    public CompletionStage<Done> rejectModel(User user, String project, String model, String version, String reason) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModelVersion(user, version,
                mdl -> mdl.withEvent(Rejected.apply(ActionMetadata.apply(user), reason))));
    }

    @Override
    public CompletionStage<Done> requestModelReview(User user, String project, String model, String version) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModelVersion(user, version,
                mdl -> mdl.withEvent(ReviewRequested.apply(ActionMetadata
                    .apply(user)))));
    }

    @Override
    public CompletionStage<Done> reportCodeQuality(User user, String project, String model, String version,
                                                   String commit, int score, int coverage, List<CodeIssue> issues) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModelVersion(user, version, mdl -> {
                var quality = CodeQuality.apply(Instant.now(), commit, score, coverage, issues);
                return mdl.withCodeQuality(quality);
            }));
    }

    @Override
    public CompletionStage<Done> runExplainer(User user, String project, String model, String version) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String project, String model) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(ModelEntity::getLatestQuestionnaireAnswers);
    }

    /*
     * Manage model members
     */

    @Override
    public CompletionStage<Done> grantModelRole(User user, String project, String model,
                                                UserAuthorization authorization, ModelMemberRole role) {
        // TODO mw: Check membership of project

        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.addMember(user, authorization, role));
    }

    @Override
    public CompletionStage<Done> revokeModelRole(User user, String workspace, String model,
                                                 UserAuthorization authorization) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.removeMember(user, authorization));
    }

    /*
     * Manage members
     */
    @Override
    public CompletionStage<Done> grant(User user, String workspace, Authorization authorization,
                                       WorkspaceMemberRole role) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(project -> project.members().addMember(user, authorization, role));
    }

    @Override
    public CompletionStage<Done> revoke(User user, String workspace, Authorization authorization) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(project -> project.members().removeMember(user, authorization));
    }

    @Override
    public CompletionStage<Done> redeployInfrastructure(User user) {
        return workspaces.redeployInfrastructure();
    }

}
