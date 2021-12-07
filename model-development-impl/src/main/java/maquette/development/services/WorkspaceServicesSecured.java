package maquette.development.services;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.common.exceptions.NotAuthorizedException;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;
import maquette.development.values.EnvironmentType;
import maquette.development.values.Workspace;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;
import maquette.development.values.model.governance.CodeIssue;

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
    public CompletionStage<Done> create(User user, String name, String title, String summary) {
        if (user instanceof AuthenticatedUser) {
            return delegate.create(user, name, title, summary);
        } else {
            var message = "Only authenticated users are allowed to create new projects.";
            return CompletableFuture.failedFuture(NotAuthorizedException.apply(message));
        }
    }

    @Override
    public CompletionStage<Map<String, String>> environment(User user, String workspace,
                                                            EnvironmentType environmentType) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace))
            .thenCompose(ok -> delegate.environment(user, workspace, environmentType));
    }

    @Override
    public CompletionStage<List<WorkspaceProperties>> list(User user) {
        return delegate.list(user)
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
    public CompletionStage<Workspace> get(User user, String workspace) {
        return delegate
            .get(user, workspace)
            .thenCompose(project -> companion
                .filterAuthorized(project, () -> companion.isMember(user, workspace))
                .thenApply(opt -> opt.orElse(project
                    .withMembers(List.of())
                    .withSandboxes(List.of())
                    .withDataAccessRequests(List.of()))));
    }

    @Override
    public CompletionStage<Done> remove(User user, String workspace) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.remove(user, workspace));
    }

    @Override
    public CompletionStage<Done> update(User user, String workspace, String updatedName, String title, String summary) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.update(user, workspace, updatedName, title, summary));
    }

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String workspace) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.getModels(user, workspace));
    }

    @Override
    public CompletionStage<Model> getModel(User user, String workspace, String model) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.getModel(user, workspace, model));
    }

    @Override
    public CompletionStage<Done> updateModel(User user, String workspace, String model, String title,
                                             String description) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.updateModel(user, workspace, model, title, description));
    }

    @Override
    public CompletionStage<Done> updateModelVersion(User user, String workspace, String model, String version,
                                                    String description) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.updateModelVersion(user, workspace, model, version, description));
    }

    @Override
    public CompletionStage<Done> answerQuestionnaire(User user, String workspace, String model, String version,
                                                     JsonNode responses) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.answerQuestionnaire(user, workspace, model, version, responses));
    }

    @Override
    public CompletionStage<Done> approveModel(User user, String workspace, String model, String version) {
        // TODO mw: Check auth
        return delegate.approveModel(user, workspace, model, version);
    }

    @Override
    public CompletionStage<Done> promoteModel(User user, String workspace, String model, String version, String stage) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.promoteModel(user, workspace, model, version, stage));
    }

    @Override
    public CompletionStage<Done> rejectModel(User user, String workspace, String model, String version, String reason) {
        // TODO mw: Check auth
        return delegate.rejectModel(user, workspace, model, version, reason);
    }

    @Override
    public CompletionStage<Done> requestModelReview(User user, String workspace, String model, String version) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.requestModelReview(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Done> reportCodeQuality(User user, String workspace, String model, String version,
                                                   String commit, int score, int coverage, List<CodeIssue> issues) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.reportCodeQuality(user, workspace, model, version, commit, score, coverage,
                issues));
    }

    @Override
    public CompletionStage<Done> runExplainer(User user, String workspace, String model, String version) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.runExplainer(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String workspace,
                                                                             String model) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.MEMBER))
            .thenCompose(ok -> delegate.getLatestQuestionnaireAnswers(user, workspace, model));
    }

    @Override
    public CompletionStage<Done> grantModelRole(User user, String workspace, String model,
                                                UserAuthorization authorization, ModelMemberRole role) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.grantModelRole(user, workspace, model, authorization, role));
    }

    @Override
    public CompletionStage<Done> revokeModelRole(User user, String workspace, String model,
                                                 UserAuthorization authorization) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.revokeModelRole(user, workspace, model, authorization));
    }

    @Override
    public CompletionStage<Done> grant(User user, String workspace, Authorization authorization,
                                       WorkspaceMemberRole role) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.grant(user, workspace, authorization, role));
    }

    @Override
    public CompletionStage<Done> revoke(User user, String workspace, Authorization authorization) {
        return companion
            .isAuthorized(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.revoke(user, workspace, authorization));
    }
}
