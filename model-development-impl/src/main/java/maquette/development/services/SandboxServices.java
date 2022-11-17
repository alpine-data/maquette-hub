package maquette.development.services;

import akka.Done;
import maquette.core.modules.users.model.UserAuthenticationToken;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SandboxServices {

    CompletionStage<SandboxProperties> createSandbox(
        User user, String workspace, String name, String comment, Optional<VolumeDefinition> volume,
        List<StackConfiguration> stacks);

    /**
     * Returns a valid user authentication token for the owner of the sandbox.
     *
     * @param workspaceId The workspace unique id to which the sandbox belongs.
     * @param sandboxId   The sandbox unique id.
     * @param stackHash   The secret stack hash of a sandbox.
     * @return The authentication token of the sandbox owner.
     */
    CompletionStage<UserAuthenticationToken> getAuthenticationToken(UID workspaceId, UID sandboxId, String stackHash);

    CompletionStage<Sandbox> getSandbox(User user, String workspace, String sandbox);

    CompletionStage<List<StackProperties>> getStacks(User user);

    CompletionStage<List<SandboxProperties>> getSandboxes(User user, String workspace);

    CompletionStage<Done> removeSandbox(User user, String workspace, String sandbox);

}
