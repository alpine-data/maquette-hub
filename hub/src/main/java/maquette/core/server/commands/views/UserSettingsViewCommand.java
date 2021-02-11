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
import maquette.core.server.views.UserSettingsView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class UserSettingsViewCommand implements Command {

   String userId;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var profileCS = services
         .getUserServices()
         .getProfile(user, userId);

      var settingsCS = services
         .getUserServices()
         .getSettings(user, userId);

      return Operators.compose(profileCS, settingsCS, UserSettingsView::apply);
   }

   @Override
   public Command example() {
      return apply("some-stream");
   }

}
