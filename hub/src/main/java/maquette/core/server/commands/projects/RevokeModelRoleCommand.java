package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class RevokeModelRoleCommand implements Command {

   String project;

   String model;

   UserAuthorization authorization;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getProjectServices()
         .revokeModelRole(user, project, model, authorization)
         .thenApply(done -> MessageResult.apply("Successfully revoked role"));
   }

   @Override
   public Command example() {
      return apply("some-project", "some-model", UserAuthorization.apply("edgar"));
   }

}
