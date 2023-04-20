package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.common.exceptions.NotAuthorizedException;
import maquette.core.modules.applications.model.Application;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.OauthProxyUser;
import maquette.core.values.user.User;
import maquette.development.values.EnvironmentType;
import maquette.development.values.Workspace;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.mlproject.MLProjectType;
import maquette.development.values.mlproject.MachineLearningProject;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.ModelVersionStage;
import maquette.development.values.model.governance.CodeIssue;
import maquette.development.values.model.services.ModelServiceProperties;
import maquette.development.values.stacks.VolumeProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class WorkspaceServicesSecured implements WorkspaceServices {

    private final WorkspaceServices delegate;

    private final WorkspaceServicesCompanion companion;

    @Override
    public CompletionStage<Done> create(User user,
                                        String name,
                                        String title,
                                        String summary) {
        if (user instanceof AuthenticatedUser) {
            return delegate.create(user, name, title, summary);
        } else {
            var message = "Only authenticated users are allowed to create new projects.";
            return CompletableFuture.failedFuture(NotAuthorizedException.apply(message));
        }
    }

    @Override
    public CompletionStage<MachineLearningProject> createMachineLearningProject(User user, String workspace,
                                                                                String projectName, MLProjectType templateType) {
        return companion
            .withAuthorization(
                () -> companion.isMember(user, workspace)
            )
            .thenCompose(ok -> delegate.createMachineLearningProject(
                user, workspace, projectName, templateType
            ));
    }

    @Override
    public CompletionStage<ModelServiceProperties> createModelService(User user, String workspace, String model,
                                                                      String version, String service) {
        return companion
            .withAuthorization(
                () -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.createModelService(
                user, workspace, model, version, service
            ));
    }

    @Override
    public CompletionStage<Map<String, String>> getEnvironment(User user,
                                                               String workspace,
                                                               EnvironmentType environmentType, boolean returnBase64) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.getEnvironment(user, workspace, environmentType, returnBase64));
    }

    @Override
    public CompletionStage<Map<String, String>> getEnvironment(User user, String workspace, EnvironmentType environmentType) {
        return WorkspaceServices.super.getEnvironment(user, workspace, environmentType);
    }

    @Override
    public CompletionStage<List<WorkspaceProperties>> list(User user) {
        return delegate
            .list(user)
            .thenCompose(workspaces -> Operators.allOf(workspaces
                .stream()
                .map(workspace -> companion.filterMember(user, workspace.getName(), workspace))))
            .thenApply(workspaces -> workspaces
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<Workspace> get(User user,
                                          String workspace) {
        return delegate
            .get(user, workspace)
            .thenCompose(project -> companion
                .filterAuthorized(project, () -> companion.isMember(user, workspace))
                .thenApply(opt -> opt.orElse(project
                    .withMembers(List.of())
                    .withSandboxes(List.of())
                    .withAssets(List.of())
                    .withDataAccessRequests(List.of()))));
    }

    @Override
    public CompletionStage<Done> remove(User user,
                                        String workspace) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.remove(user, workspace));
    }

    @Override
    public CompletionStage<Done> update(User user,
                                        String workspace,
                                        String updatedName,
                                        String title,
                                        String summary) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.update(user, workspace, updatedName, title, summary));
    }

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user,
                                                            String workspace) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.getModels(user, workspace));
    }

    @Override
    public CompletionStage<Model> getModel(User user,
                                           String workspace,
                                           String model) {
        return companion
            .withAuthorization(
                () -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.getModel(user, workspace, model));
    }

    @Override
    public CompletionStage<Done> updateModel(User user,
                                             String workspace,
                                             String model,
                                             String description) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.updateModel(user, workspace, model, description));
    }

    @Override
    public CompletionStage<Done> approveModel(User user,
                                              String workspace,
                                              String model,
                                              String version) {
        // TODO mw: Check auth
        return delegate.approveModel(user, workspace, model, version);
    }

    @Override
    public CompletionStage<Done> promoteModel(User user,
                                              String workspace,
                                              String model,
                                              String version,
                                              ModelVersionStage stage) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.promoteModel(user, workspace, model, version, stage));
    }

    @Override
    public CompletionStage<Done> rejectModel(User user,
                                             String workspace,
                                             String model,
                                             String version,
                                             String reason) {
        // TODO mw: Check auth
        return delegate.rejectModel(user, workspace, model, version, reason);
    }

    @Override
    public CompletionStage<Done> requestModelReview(User user,
                                                    String workspace,
                                                    String model,
                                                    String version) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.requestModelReview(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Done> reportCodeQuality(User user,
                                                   String workspace,
                                                   String model,
                                                   String version,
                                                   String commit,
                                                   int score,
                                                   int coverage,
                                                   List<CodeIssue> issues) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.reportCodeQuality(user, workspace, model, version, commit, score, coverage,
                issues));
    }

    @Override
    public CompletionStage<Done> runExplainer(User user,
                                              String workspace,
                                              String model,
                                              String version) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.runExplainer(user, workspace, model, version));
    }

    @Override
    public CompletionStage<String> getExplainer(User user, String workspace, String model, String version, String artifactPath) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.getExplainer(user, workspace, model,version, artifactPath));
    }

    @Override
    public CompletionStage<Done> grantModelRole(User user,
                                                String workspace,
                                                String model,
                                                UserAuthorization authorization,
                                                ModelMemberRole role) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.grantModelRole(user, workspace, model, authorization, role));
    }

    @Override
    public CompletionStage<Done> revokeModelRole(User user,
                                                 String workspace,
                                                 String model,
                                                 UserAuthorization authorization) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.revokeModelRole(user, workspace, model, authorization));
    }

    @Override
    public CompletionStage<Done> grant(User user,
                                       String workspace,
                                       Authorization authorization,
                                       WorkspaceMemberRole role) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.grant(user, workspace, authorization, role));
    }

    @Override
    public CompletionStage<Done> revoke(User user,
                                        String workspace,
                                        Authorization authorization) {
        return companion
            .withAuthorization(() -> {
                var notSameUserCS = CompletableFuture.completedStage(!user
                    .getDisplayName()
                    .equals(authorization.getName()));
                var isAuthorizedCS = companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN);
                return Operators.compose(notSameUserCS, isAuthorizedCS,
                    (notSameUser, isAuthorized) -> notSameUser && isAuthorized);
            })
            .thenCompose(ok -> delegate.revoke(user, workspace, authorization));
    }

    @Override
    public CompletionStage<Done> redeployInfrastructure(User user) {
        return delegate.redeployInfrastructure(user);
    }

    @Override
    public CompletionStage<List<VolumeProperties>> getVolumes(User user, String workspace) {
        return companion
            .withAuthorization(
                () -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.getVolumes(user, workspace));
    }

    @Override
    public CompletionStage<Application> createApplication(MaquetteRuntime runtime, User user, String workspaceName,
                                                          String name,
                                                          String metaInfo) {

        return companion
            .withAuthorization(() -> companion.isMember(user, workspaceName, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.createApplication(runtime, user, workspaceName, name, metaInfo));
    }

    @Override
    public CompletionStage<Done> renewApplicationSecret(MaquetteRuntime runtime, User user, String workspaceName,
                                                        String name) {

        return companion
            .withAuthorization(() -> companion.isMember(user, workspaceName, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.renewApplicationSecret(runtime, user, workspaceName, name));
    }

    @Override
    public CompletionStage<Done> removeApplication(MaquetteRuntime runtime, User user, String workspaceName,
                                                   String applicationName) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspaceName, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.removeApplication(runtime, user, workspaceName, applicationName));
    }

    @Override
    public CompletionStage<Application> getOauthSelfApplication(MaquetteRuntime runtime, User user) {
        return companion
            // only available to proxy user
            .withAuthorization(() -> CompletableFuture.completedFuture(user instanceof OauthProxyUser))
            .thenCompose(ok -> delegate.getOauthSelfApplication(runtime, user));
    }

    @Override
    public CompletionStage<List<Application>> findApplicationsInWorkspace(MaquetteRuntime runtime, User user,
                                                                          String workspaceName) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspaceName))
            .thenCompose(ok -> delegate.findApplicationsInWorkspace(runtime, user, workspaceName));
    }
}
