package maquette.core.server.commands.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateCustomDataAssetSettingsCommand implements Command {

   String name;

   JsonNode customSettings;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataAssetServices()
         .get(user, name)
         .thenApply(DataAsset::getProperties)
         .thenApply(properties -> runtime.getDataAssetProviders().getByName(properties.getType()))
         .thenCompose(assetProvider -> {
            var customSettings = Operators.suppressExceptions(() ->
               runtime.getObjectMapper().treeToValue(this.customSettings, assetProvider.getSettingsType()));

            return services
               .getDataAssetServices()
               .updateCustomSettings(user, name, customSettings)
               .thenApply(done -> MessageResult.apply("Successfully updated data asset."));
         });
   }

   @Override
   public Command example() {
      return apply("some-dataset", null);
   }

}
