package maquette.core.server.commands.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAssetMetadata;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.DataZone;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateDataAssetCommand implements Command {

   String name;

   DataAssetMetadata metadata;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDataAssetServices()
         .update(user, name, metadata)
         .thenApply(done -> MessageResult.apply("Successfully updated data asset."));
   }

   @Override
   public Command example() {
      var meta = DataAssetMetadata.apply(
         "some-dataset", "title", Operators.lorem(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW);

      return apply("some-dataset", meta);
   }

}
