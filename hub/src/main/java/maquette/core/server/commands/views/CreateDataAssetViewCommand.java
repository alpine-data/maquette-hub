package maquette.core.server.commands.views;

import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CreateDataAssetView;
import maquette.core.server.views.CreateSandboxView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public final class CreateDataAssetViewCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var profileCS = services
         .getUserServices()
         .getProfile(user)
         .thenApply(UserProfile::getId);

      var usersCS = services
         .getUserServices()
         .getUsers(user);

      var ownerCS = services
         .getConfigurationServices()
         .getDefaultDataOwner();

      return Operators.compose(profileCS, usersCS, ownerCS, CreateDataAssetView::apply);
   }

   @Override
   public Command example() {
      return new CreateDataAssetViewCommand();
   }

}
