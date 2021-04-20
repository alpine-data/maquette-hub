package maquette.core.services.sandboxes;

import maquette.core.entities.projects.model.sandboxes.Sandbox;
import maquette.core.entities.projects.model.sandboxes.SandboxProperties;
import maquette.core.entities.projects.model.sandboxes.stacks.StackConfiguration;
import maquette.core.entities.projects.model.sandboxes.stacks.StackProperties;
import maquette.core.entities.projects.model.sandboxes.volumes.VolumeDefinition;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SandboxServices {

   CompletionStage<Sandbox> createSandbox(
      User user, String project, String name, VolumeDefinition volume, List<StackConfiguration> stacks);

   CompletionStage<Sandbox> getSandbox(User user, String project, String sandbox);

   CompletionStage<List<StackProperties>> getStacks(User user);

   CompletionStage<List<SandboxProperties>> getSandboxes(User user, String project);

}
