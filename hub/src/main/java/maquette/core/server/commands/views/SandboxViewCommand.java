package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.ProjectView;
import maquette.core.server.views.SandboxView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class SandboxViewCommand implements Command {

   String project;

   String sandbox;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var projectCS = services
         .getProjectServices()
         .get(user, project);

      var sandboxCS = services
         .getSandboxServices()
         .getSandbox(user, project, sandbox);

      var stacksCS = services
         .getSandboxServices()
         .getStacks(user);

      return Operators.compose(projectCS, sandboxCS, stacksCS, SandboxView::apply);
   }

   @Override
   public Command example() {
      return apply("some-project", "some-sandbox");
   }

}
