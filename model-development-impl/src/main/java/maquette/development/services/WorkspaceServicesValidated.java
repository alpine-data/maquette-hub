package maquette.development.services;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
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
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class WorkspaceServicesValidated implements WorkspaceServices {

    private final WorkspaceServices delegate;

    @Override
    public CompletionStage<Done> create(User user, String name, String title, String summary) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("name", name, NonEmptyStringValidator.apply(3))
            .validate("title", title, NonEmptyStringValidator.apply(3))
            .validate("summary", summary, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.create(user, name, title, summary));
    }

    @Override
    public CompletionStage<Map<String, String>> environment(User user, String workspace,
                                                            EnvironmentType environmentType) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("environmentType", environmentType, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.environment(user, workspace, environmentType));
    }

    @Override
    public CompletionStage<List<WorkspaceProperties>> list(User user) {
        return delegate.list(user);
    }

    @Override
    public CompletionStage<Workspace> get(User user, String workspace) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.get(user, workspace));
    }

    @Override
    public CompletionStage<Done> remove(User user, String workspace) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.remove(user, workspace));
    }

    @Override
    public CompletionStage<Done> update(User user, String workspace, String updatedName, String title, String summary) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("updateName", updatedName, NonEmptyStringValidator.apply(3))
            .validate("title", title, NonEmptyStringValidator.apply(3))
            .validate("summary", summary, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.update(user, workspace, updatedName, title, summary));
    }

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String workspace) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.getModels(user, workspace));
    }

    @Override
    public CompletionStage<Model> getModel(User user, String workspace, String model) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.getModel(user, workspace, model));
    }

    @Override
    public CompletionStage<Done> updateModel(User user, String workspace, String model, String title,
                                             String description) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("title", title, NonEmptyStringValidator.apply(3))
            .validate("description", description, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.updateModel(user, workspace, model, title, description));
    }

    @Override
    public CompletionStage<Done> updateModelVersion(User user, String workspace, String model, String version,
                                                    String description) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .validate("description", description, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.updateModelVersion(user, workspace, model, version, description));
    }

    @Override
    public CompletionStage<Done> answerQuestionnaire(User user, String workspace, String model, String version,
                                                     JsonNode responses) {

        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .validate("response", responses, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.answerQuestionnaire(user, workspace, model, version, responses));
    }

    @Override
    public CompletionStage<Done> approveModel(User user, String workspace, String model, String version) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.approveModel(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Done> promoteModel(User user, String workspace, String model, String version, String stage) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .validate("stage", stage, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.promoteModel(user, workspace, model, version, stage));
    }

    @Override
    public CompletionStage<Done> rejectModel(User user, String workspace, String model, String version, String reason) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .validate("reason", reason, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.rejectModel(user, workspace, model, version, reason));
    }

    @Override
    public CompletionStage<Done> requestModelReview(User user, String workspace, String model, String version) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.requestModelReview(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Done> reportCodeQuality(User user, String workspace, String model, String version,
                                                   String commit, int score, int coverage, List<CodeIssue> issues) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .validate("commit", commit, NonEmptyStringValidator.apply(3))
            .validate("issues", issues, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(
                done -> delegate.reportCodeQuality(user, workspace, model, version, commit, score, coverage, issues));
    }

    @Override
    public CompletionStage<Done> runExplainer(User user, String workspace, String model, String version) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.runExplainer(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String workspace,
                                                                             String model) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.getLatestQuestionnaireAnswers(user, workspace, model));
    }

    @Override
    public CompletionStage<Done> grantModelRole(User user, String workspace, String model,
                                                UserAuthorization authorization, ModelMemberRole role) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("authorization", authorization, NotNullValidator.apply())
            .validate("role", role, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.grantModelRole(user, workspace, model, authorization, role));
    }

    @Override
    public CompletionStage<Done> revokeModelRole(User user, String workspace, String model,
                                                 UserAuthorization authorization) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("authorization", authorization, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.revokeModelRole(user, workspace, model, authorization));
    }

    @Override
    public CompletionStage<Done> grant(User user, String workspace, Authorization authorization,
                                       WorkspaceMemberRole role) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("authorization", authorization, NotNullValidator.apply())
            .validate("role", role, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.grant(user, workspace, authorization, role));
    }

    @Override
    public CompletionStage<Done> revoke(User user, String workspace, Authorization authorization) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("authorization", authorization, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.revoke(user, workspace, authorization));
    }

    @Override
    public CompletionStage<Done> redeployInfrastructure(User user) {
        return delegate.redeployInfrastructure(user);
    }

}
