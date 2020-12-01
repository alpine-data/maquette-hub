package maquette.core.services.sandboxes;

import lombok.AllArgsConstructor;
import maquette.core.entities.sandboxes.model.Sandbox;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.StackConfiguration;
import maquette.core.entities.sandboxes.model.stacks.StackProperties;
import maquette.core.services.projects.ProjectCompanion;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class SandboxServicesSecured implements SandboxServices {

   private final SandboxServices delegate;

   private final ProjectCompanion companion;

   @Override
   public CompletionStage<Sandbox> createSandbox(User user, String project, String name, List<StackConfiguration> stacks) {
      return companion
         .withAuthorization(() -> companion.isMember(user, project))
         .thenCompose(ok -> delegate.createSandbox(user, project, name, stacks));
   }

   @Override
   public CompletionStage<Sandbox> getSandbox(User user, String project, String sandbox) {
      return companion
         .withAuthorization(() -> companion.isMember(user, project))
         .thenCompose(ok -> delegate.getSandbox(user, project, sandbox));
   }

   @Override
   public CompletionStage<List<StackProperties>> getStacks(User user) {
      return delegate.getStacks(user);
   }

   @Override
   public CompletionStage<List<SandboxProperties>> getSandboxes(User user, String project) {
      return companion
         .withAuthorization(() -> companion.isMember(user, project))
         .thenCompose(ok -> delegate.getSandboxes(user, project));
   }

}
