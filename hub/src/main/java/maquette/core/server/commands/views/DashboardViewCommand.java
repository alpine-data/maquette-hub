package maquette.core.server.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.DashboardView;
import maquette.core.server.views.DataShopView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
public class DashboardViewCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getUserServices()
         .getProjects(user)
         .thenApply(DashboardView::apply);
   }

   @Override
   public Command example() {
      return null;
   }

}
