package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.UserProfileView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UserProfileViewCommand implements Command {

   String userId;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var profileCS = services
         .getUserServices()
         .getProfile(user, userId);

      var projectsCS = services
         .getUserServices()
         .getUserProjects(user, userId);

      var dataAssetsCS = services
         .getUserServices()
         .getUserDataAssets(user, userId);

      return Operators.compose(profileCS, projectsCS, dataAssetsCS, (profile, projects, dataAssets) -> {
         var isOwnProfile = (user instanceof AuthenticatedUser) && ((AuthenticatedUser) user).getId().equals(userId);
         return UserProfileView.apply(profile, isOwnProfile, projects, dataAssets);
      });
   }

   @Override
   public Command example() {
      return apply("some-stream");
   }

}
