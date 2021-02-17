package maquette.core.server.commands.data.streams;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.streams.model.DurationUnit;
import maquette.core.entities.data.streams.model.Retention;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.DataZone;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateStreamCommand implements Command {

   String title;

   String name;

   String summary;

   int retentionDuration;

   DurationUnit retentionUnit;

   Schema schema;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   DataZone zone;

   String owner;

   String steward;


   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getStreamServices()
         .create(user, title, name, summary, Retention.apply(
            retentionDuration, retentionUnit), schema,
            visibility, classification, personalInformation, zone,
            UserAuthorization.apply(owner), UserAuthorization.apply(steward))
         .thenApply(pid -> MessageResult.apply("Successfully created stream `%s`", name));
   }

   @Override
   public Command example() {
      return apply(
         "Some Stream", "some-stream", Operators.lorem(),
         6, DurationUnit.HOURS, SchemaBuilder
            .record("Test")
            .fields()
            .requiredLong("id")
            .requiredString("color")
            .optionalDouble("price")
            .endRecord(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE, DataZone.RAW, "alice", "bob");
   }

}
