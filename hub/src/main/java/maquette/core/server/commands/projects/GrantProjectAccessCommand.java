package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class GrantProjectAccessCommand implements Command {

   String project;

   Authorization authorization;

   ProjectMemberRole role;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .grant(user, project, authorization, role)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply("some-project", UserAuthorization.apply("edgar"), ProjectMemberRole.MEMBER);
   }

}
