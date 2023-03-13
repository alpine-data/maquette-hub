package maquette.development.services;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.modules.applications.ApplicationModule;
import maquette.core.modules.applications.model.Application;
import maquette.core.modules.users.UserEntities;
import maquette.core.modules.users.UserEntity;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.ApplicationUser;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.OauthProxyUser;
import maquette.core.values.user.User;
import maquette.development.entities.*;
import maquette.development.ports.DataAssetsServicePort;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.development.values.EnvironmentType;
import maquette.development.values.Workspace;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.model.*;
import maquette.development.values.model.events.Approved;
import maquette.development.values.model.events.Rejected;
import maquette.development.values.model.events.ReviewRequested;
import maquette.development.values.model.governance.CodeIssue;
import maquette.development.values.model.governance.CodeQuality;
import maquette.development.values.model.services.ModelServiceProperties;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.stacks.VolumeProperties;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkspaceServicesImpl implements WorkspaceServices {

    WorkspaceEntities workspaces;

    SandboxEntities sandboxes;

    UserEntities users;

    DataAssetsServicePort dataAssets;

    ModelOperationsPort modelOperations;

    public static WorkspaceServicesImpl apply(
        WorkspaceEntities projects, SandboxEntities sandboxes, UserEntities users, DataAssetsServicePort dataAssets,
        ModelOperationsPort modelOperations) {

        return new WorkspaceServicesImpl(projects, sandboxes, users, dataAssets, modelOperations);
    }

    @Override
    public CompletionStage<Done> create(User executor, String name, String title, String summary) {
        return workspaces
            .createWorkspace(executor, name, title, summary)
            .thenCompose(project -> workspaces.getWorkspaceById(project.getId()))
            .thenCompose(workspace -> {
                var adminAddedCS = workspace
                    .members()
                    .addMember(executor, executor.toAuthorization(), WorkspaceMemberRole.ADMIN);

                var mlFlowInitializedCS = workspace.initializeMlflowEnvironment();

                return Operators.compose(
                    adminAddedCS, mlFlowInitializedCS,
                    (adminAdded, mlFlowInitialized) -> Done.getInstance());
            });
    }

    @Override
    public CompletionStage<ModelServiceProperties> createModelService(User user, String workspace, String model,
                                                                      String version, String service) {

        if (!(user instanceof AuthenticatedUser)) {
            /*
             * `WorkspaceServicesSecured` checks whether the user is an authenticated user.
             * This function can only be called by individual users.
             */

            throw new IllegalArgumentException("This method can only be called by authenticated users.");
        }

        var workspaceEntityCS = workspaces
            .getWorkspaceByName(workspace);

        var profileCS = users
            .getUserById(((AuthenticatedUser) user).getId())
            .thenCompose(UserEntity::getProfileById);

        var modelEntityCS = workspaceEntityCS
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model));

        return Operators
            .compose(workspaceEntityCS, profileCS, modelEntityCS, (workspaceEntity, profile, modelEntity) -> {
                var mlflowId = WorkspaceEntity.getMlflowStackName(workspaceEntity.getId());

                return modelEntity.createService(version, service, mlflowId, profile.getName(), profile.getEmail());
            })
            .thenCompose(cs -> cs);
    }

    @Override
    public CompletionStage<Map<String, String>> getEnvironment(User user, String workspace, EnvironmentType type,
                                                               boolean returnBase64) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(wks -> wks.getEnvironment(user, type))
            .thenApply(parameters -> Maps.transformValues(parameters, (v) -> {
                if (org.apache.commons.codec.binary.Base64.isBase64(v) && !returnBase64) {
                    try {
                        return new String(Base64.getDecoder().decode(v));
                    } catch (Exception e) {
                        return v;
                    }
                } else {
                    return v;
                }
            }));
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
                var propertiesCS = workspace
                    .getProperties()
                    .thenApply(workspaceProperties -> workspaceProperties
                        .withVolumes(Optional.ofNullable(workspaceProperties.getVolumes())
                            .orElseGet(Collections::emptyList)
                            .stream()
                            .filter(volume -> volume
                                .getUser()
                                .getValue()
                                .equals(user.getDisplayName()))
                            .collect(Collectors.toList())));

                var membersCS = workspace
                    .members()
                    .getMembers();

                var accessRequestsCS = dataAssets.findDataAccessRequestsByWorkspace(workspace.getId());

                var dataAssetsCS = dataAssets.findDataAssetsByWorkspace(workspace.getId());

                var sandboxesCS = sandboxes
                    .listSandboxes(workspace.getId())
                    .thenCompose(sdbxProperties -> Operators.allOf(
                        sdbxProperties
                            .stream()
                            .filter(properties -> properties
                                .getCreated()
                                .getBy()
                                .equals(user.getDisplayName()))
                            .map(properties -> sandboxes
                                .getSandboxById(workspace.getId(), properties.getId())
                                .thenCompose(SandboxEntity::getState)
                                .thenApply(stacks -> Sandbox.apply(properties, stacks)))));

                var mlflowStatusCS = workspace
                    .getMlflowStatus()
                    .thenApply(opt -> opt.orElse(null));

                return Operators.compose(
                    propertiesCS, accessRequestsCS, membersCS, dataAssetsCS, sandboxesCS, mlflowStatusCS,
                    Workspace::apply);
            });
    }

    @Override
    public CompletionStage<Done> remove(User user, String workspace) {
        return workspaces
            .findWorkspaceByName(workspace)
            .thenCompose(maybeWorkspace -> {
                if (maybeWorkspace.isPresent()) {
                    var workspaceId = maybeWorkspace
                        .get()
                        .getId();

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
        var workspaceCS = workspaces
            .getWorkspaceByName(project);

        var modelEntityCS = workspaceCS
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model));

        var workspaceMembersCS = workspaceCS
            .thenCompose(p -> p
                .members()
                .getMembers());

        /*
         * The model URL consists of "{WORKSPACE_UID}/{MODEL_NAME}". This convention must also be used when
         * registering a model
         * from within an Azure DevOps Pipeline. Otherwise, we cannot find service instances.
         */
        var modelUrlCS = Operators.compose(workspaceCS, modelEntityCS.thenCompose(ModelEntity::getProperties),
            (workspace, modelProperties) -> WorkspaceEntity.getMlflowStackName(workspace.getId()) + "/" + modelProperties.getName());

        return Operators
            .compose(modelEntityCS, workspaceMembersCS, modelUrlCS, (modelEntity, projectMembers, modelUrl) -> {
                var propertiesCS = modelEntity.getProperties();
                var membersCS = modelEntity.getMembers();
                var permissionsCS = membersCS
                    .thenApply(members -> ModelMembersCompanion.apply(members, projectMembers))
                    .thenApply(comp -> comp.getDataAssetPermissions(user));
                var servicesCS = modelEntity.getProperties()
                    .thenCompose(properties -> modelOperations.getServices(modelUrl));

                return Operators.compose(propertiesCS, membersCS, permissionsCS, servicesCS, Model::fromProperties);
            })
            .thenCompose(cs -> cs);
    }

    @Override
    public CompletionStage<Done> updateModel(User user, String project, String model,
                                             String description) {
        return workspaces
            .getWorkspaceByName(project)
            .thenCompose(WorkspaceEntity::getModels)
            .thenApply(models -> models.getModel(model))
            .thenCompose(m -> m.updateModel(user, description));
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
    public CompletionStage<Done> promoteModel(User user, String project, String model, String version,
                                              ModelVersionStage stage) {
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
            .thenCompose(project -> project
                .members()
                .addMember(user, authorization, role));
    }

    @Override
    public CompletionStage<Done> revoke(User user, String workspace, Authorization authorization) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(project -> project
                .members()
                .removeMember(user, authorization));
    }

    @Override
    public CompletionStage<Done> redeployInfrastructure(User user) {
        return workspaces.redeployInfrastructure();
    }

    @Override
    public CompletionStage<List<VolumeProperties>> getVolumes(User user, String workspace) {
        return workspaces
            .getWorkspaceByName(workspace)
            .thenCompose(entity -> entity.getVolumes(user));
    }

    private String generateRandomKey(int strength) {
        byte[] secret = new byte[strength];
        var random = new SecureRandom();
        random.nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret);
    }

    @Override
    public CompletionStage<Application> createApplication(MaquetteRuntime runtime,
                                                          User user,
                                                          String workspaceName,
                                                          String name,
                                                          String metaInfo) {
        final var applications = runtime.getModule(ApplicationModule.class).getApplications();
        final var secretStrength = runtime.getConfig().getCore().getApplicationSecretStrength();
        return workspaces
            .getWorkspaceByName(workspaceName)
            .thenCompose(entity ->
                applications
                    .getApplicationByNameAndWorkspaceId(name, entity.getId())
                    .thenCompose(existingApp -> {
                        // check if app already exists to not create it twice
                        if (existingApp.isPresent())
                            return CompletableFuture.completedFuture(existingApp.get());
                        // otherwise create new app with new randomly generated ID
                        final var app = Application.apply(
                            UID.apply(), entity.getId(), generateRandomKey(secretStrength), name, metaInfo
                        );
                        final var appUser = ApplicationUser.apply(app.getId(), Lists.newArrayList());
                        return applications
                            .save(app)
                            .thenCompose(result ->
                                entity
                                    .members()
                                    .addMember(user, appUser.toAuthorization(), WorkspaceMemberRole.MEMBER)
                            )
                            .thenApply(result -> app);
                    })
            );
    }

    @Override
    public CompletionStage<Done> renewApplicationSecret(MaquetteRuntime runtime,
                                                        User user,
                                                        String workspaceName,
                                                        String name) {
        final var applications = runtime.getModule(ApplicationModule.class).getApplications();
        final var secretStrength = runtime.getConfig().getCore().getApplicationSecretStrength();
        return workspaces
            .getWorkspaceByName(workspaceName)
            .thenCompose(entity -> applications.getApplicationByNameAndWorkspaceId(name, entity.getId()))
            .thenCompose(application ->
                application
                    .map(entity -> applications.save(entity.withSecret(generateRandomKey(secretStrength))))
                    .orElse(CompletableFuture.completedFuture(Done.getInstance())));
    }

    @Override
    public CompletionStage<Done> removeApplication(MaquetteRuntime runtime, User user, String workspaceName,
                                                   String applicationName) {
        final var applications = runtime.getModule(ApplicationModule.class).getApplications();
        return workspaces
            .getWorkspaceByName(workspaceName)
            .thenCompose(entity ->
                applications.getApplicationByNameAndWorkspaceId(applicationName, entity.getId())
                    .thenCompose(app ->
                        app
                            .map(result -> {
                                final var appUser = ApplicationUser.apply(result.getId(), Lists.newArrayList());
                                return entity.members()
                                    .removeMember(user, appUser.toAuthorization())
                                    .thenCompose(removeResult -> applications.removeApplication(result));
                            })
                            .orElse(CompletableFuture.completedFuture(Done.getInstance()))
                    )
            );
    }

    @Override
    public CompletionStage<Application> getOauthSelfApplication(MaquetteRuntime runtime, User user) {
        final var applications = runtime.getModule(ApplicationModule.class).getApplications();
        final var self = (OauthProxyUser) user;
        return workspaces
            .getWorkspaceByName(self.getWorkspace())
            .thenCompose(entity ->
                applications
                    .getApplicationByNameAndWorkspaceId(self.getName(), entity.getId())
                    .thenApply(Optional::orElseThrow)
            );
    }

    @Override
    public CompletionStage<List<Application>> findApplicationsInWorkspace(MaquetteRuntime runtime, User user,
                                                                          String workspaceName) {
        final var applications = runtime.getModule(ApplicationModule.class).getApplications();
        return workspaces
            .getWorkspaceByName(workspaceName)
            .thenCompose(entity -> applications.findByWorkspaceId(entity.getId()));
    }

}
