package maquette.core.server.commands.data.datasources.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetDataSourceDataAccessRequestCommand implements Command {

   String asset;

   UID id;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataSourceServices()
         .getDataAccessRequest(user, asset, id)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return apply("some-data-source", UID.apply("1"));
   }

}
