package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.values.user.User;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkSpaceMlflowView;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class CentralModelRegistryServicesValidated implements CentralModelRegistryServices {

    CentralModelRegistryServices delegate;

    @Override
    public CompletionStage<Model> getModel(User user, String modelName) {
        return delegate.getModel(user, modelName);
    }

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String search) {
        return delegate.getModels(user, search);
    }

    @Override
    public CompletionStage<String> getExplainer(User user, String workspace, String model, String version, String artifactPath) {
        return delegate.getExplainer(user, workspace, model, version, artifactPath);
    }

    @Override
    public CompletionStage<WorkSpaceMlflowView> getWorkspaceForView() {
        return delegate.getWorkspaceForView();
    }

    @Override
    public CompletionStage<Done> importModel(User user, String workspace, String model, String version) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("model", model, NonEmptyStringValidator.apply(3))
            .validate("version", version, NotNullValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.importModel(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Done> initialize() {
        return delegate.initialize();
    }

    @Override
    public CompletionStage<Map<String, String>> getEnvironment(User user, String workspace, EnvironmentType environmentType, boolean returnBase64) {
        return delegate.getEnvironment(user, workspace, environmentType, returnBase64);
    }
}
