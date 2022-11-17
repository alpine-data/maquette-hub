package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.common.validation.api.FluentValidation;
import maquette.core.common.validation.validators.NonEmptyListValidator;
import maquette.core.common.validation.validators.NonEmptyStringValidator;
import maquette.core.common.validation.validators.NotNullValidator;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesValidated implements SandboxServices {

    private final SandboxServices delegate;

    @Override
    public CompletionStage<SandboxProperties> createSandbox(
        User user, String workspace, String name, String comment,
        Optional<VolumeDefinition> volume, List<StackConfiguration> stacks) {

        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("comment", comment, NonEmptyStringValidator.apply(3))
            .validate("stacks", stacks, NonEmptyListValidator.apply())
            .checkAndFail()
            .thenCompose(done -> {
                String nameValidated;

                if (Objects.isNull(name) || name
                    .trim()
                    .equals("")) {
                    nameValidated = Operators.random_name();
                } else {
                    nameValidated = name;
                }

                return delegate.createSandbox(user, workspace, nameValidated, comment, volume, stacks);
            });
    }

    @Override
    public CompletionStage<UserAuthenticationToken> getAuthenticationToken(UID workspaceId, UID sandboxId,
                                                                           String stackHash) {
        return FluentValidation
            .apply()
            .validate("workspace", workspaceId, NotNullValidator.apply())
            .validate("sandbox", sandboxId, NotNullValidator.apply())
            .validate("stackHash", stackHash, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.getAuthenticationToken(workspaceId, sandboxId, stackHash));
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

    @Override
    public CompletionStage<Done> removeSandbox(User user, String workspace, String sandbox) {
        return FluentValidation
            .apply()
            .validate("user", user, NotNullValidator.apply())
            .validate("workspace", workspace, NonEmptyStringValidator.apply(3))
            .validate("sandbox", workspace, NonEmptyStringValidator.apply(3))
            .checkAndFail()
            .thenCompose(done -> delegate.removeSandbox(user, workspace, sandbox));
    }

}
