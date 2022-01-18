package maquette.development.services;

import lombok.AllArgsConstructor;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.NonEmptyListValidator;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.values.user.User;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesValidated implements  SandboxServices {

    private final SandboxServices delegate;

    @Override
    public CompletionStage<SandboxProperties> createSandbox(User user, String workspace, String name, VolumeDefinition volume, List<StackConfiguration> stacks) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("name", name, NonEmptyStringValidator.apply(3))
            .validate("volume", volume, NotNullValidator.apply())
            .validate("stacks", stacks, NonEmptyListValidator.apply())
            .checkAndFail()
            .thenCompose(done -> delegate.createSandbox(user, workspace, name, volume, stacks));
    }

    @Override
    public CompletionStage<Sandbox> getSandbox(User user, String workspace, String sandbox) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("sandbox", sandbox, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.getSandbox(user, workspace, sandbox));
    }

    @Override
    public CompletionStage<List<StackProperties>> getStacks(User user) {
        return delegate.getStacks(user);
    }

    @Override
    public CompletionStage<List<SandboxProperties>> getSandboxes(User user, String workspace) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.getSandboxes(user, workspace));
    }

}
