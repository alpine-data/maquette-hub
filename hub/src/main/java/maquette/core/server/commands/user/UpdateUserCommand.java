package maquette.core.server.commands.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.users.model.GitSettings;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.entities.users.model.UserSettings;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateUserCommand implements Command {

   UserProfile profile;

   UserSettings settings;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getUserServices()
         .updateUser(user, profile.getId(), profile, settings)
         .thenApply(done -> MessageResult.apply("Successfully updated user."));
   }

   @Override
   public Command example() {
      return apply(
         UserProfile.apply("alice", "Alice Kaye", "Data Scientist", "Lorem ipsum dolor", "alice@mail.con", "+49 12345 281 12", "Entenhausen"),
         UserSettings.apply(GitSettings.apply("username", "password", "privateSSHKey", "publicSSHKey")));
   }

}
