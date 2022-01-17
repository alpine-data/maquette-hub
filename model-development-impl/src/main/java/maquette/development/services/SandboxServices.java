package maquette.development.services;

import maquette.core.values.user.User;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.sandboxes.volumes.VolumeDefinition;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SandboxServices {

    CompletionStage<SandboxProperties> createSandbox(
        User user, String workspace, String name, VolumeDefinition volume, List<StackConfiguration> stacks);

    CompletionStage<Sandbox> getSandbox(User user, String workspace, String sandbox);

    CompletionStage<List<StackProperties>> getStacks(User user);

    CompletionStage<List<SandboxProperties>> getSandboxes(User user, String workspace);

}
