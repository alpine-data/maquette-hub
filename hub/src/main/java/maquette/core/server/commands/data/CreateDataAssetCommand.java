package maquette.core.server.commands.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.DataZone;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

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
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      Authorization ownerAuth = null;
      Authorization stewardAuth = null;

      if (owner != null) {
         ownerAuth = UserAuthorization.apply(owner);
      }

      if (steward != null) {
         stewardAuth = UserAuthorization.apply(steward);
      }

      var metadata = DataAssetMetadata
         .apply(title, name, summary, visibility, classification, personalInformation, zone);

      var dataAssetProvider = runtime.getDataAssetProviders().getByName(type);
      var customSettings = Operators.suppressExceptions(() ->
         runtime.getObjectMapper().treeToValue(this.customSettings, dataAssetProvider.getSettingsType()));

      return services
         .getDataAssetServices()
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
