package maquette.core.services.sandboxes;

import maquette.core.entities.sandboxes.model.Sandbox;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.StackConfiguration;
import maquette.core.entities.sandboxes.model.stacks.StackProperties;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SandboxServices {

   CompletionStage<Sandbox> createSandbox(User user, String project, String name, List<StackConfiguration> stacks);

   CompletionStage<Sandbox> getSandbox(User user, String project, String sandbox);

   CompletionStage<List<StackProperties>> getStacks(User user);

   CompletionStage<List<SandboxProperties>> getSandboxes(User user, String project);

}
