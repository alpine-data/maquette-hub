package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.BlacklistedValuesValidator;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.values.user.User;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class CentralModelRegistryServicesValidated implements CentralModelRegistryServices {

    CentralModelRegistryServices delegate;

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String search) {
        return delegate.getModels(user, search);
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
}
