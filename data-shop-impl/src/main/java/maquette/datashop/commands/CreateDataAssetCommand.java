package maquette.datashop.commands;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.values.metadata.*;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDataAssetCommand implements Command {

   String type;

   String title;

   String name;

   String summary;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   DataZone zone;

   String owner;

   String steward;

   JsonNode customSettings;

   @Override
   public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
      Authorization ownerAuth = null;
      Authorization stewardAuth = null;

      var shop = runtime.getModule(MaquetteDataShop.class);

      if (owner != null) {
         ownerAuth = UserAuthorization.apply(owner);
      }

      if (steward != null) {
         stewardAuth = UserAuthorization.apply(steward);
      }

      var metadata = DataAssetMetadata
         .apply(title, name, summary, visibility, classification, personalInformation, zone);

      var dataAssetProvider = shop.getProviders().getByName(type);
      var customSettings = Operators.suppressExceptions(() ->
         runtime.getObjectMapperFactory().createJsonMapper().treeToValue(this.customSettings, dataAssetProvider.getSettingsType()));

      return shop
         .getServices()
         .create(user, type, metadata, ownerAuth, stewardAuth, customSettings)
         .thenApply(pid -> MessageResult.apply("Successfully created data asset `%s`", name));
   }

   @Override
   public Command example() {
      return apply(
         "dataset", "Some Dataset", "some-dataset", Operators.lorem(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW, "alice", "bob", null);
   }

}
