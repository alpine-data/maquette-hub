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
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
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

   Retention retention;

   Schema schema;

   DataVisibility visibility;

   DataClassification classification;

   PersonalInformation personalInformation;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getStreamServices()
         .create(user, title, name, summary, retention, schema, visibility, classification, personalInformation)
         .thenApply(pid -> MessageResult.apply("Successfully created stream `%s`", name));
   }

   @Override
   public Command example() {
      return apply(
         "Some Stream", "some-stream", Operators.lorem(),
         Retention.apply(6, DurationUnit.HOURS), SchemaBuilder
               .record("Test")
               .fields()
               .requiredLong("id")
               .requiredString("color")
               .optionalDouble("price")
               .endRecord(),
         DataVisibility.PUBLIC, DataClassification.PUBLIC, PersonalInformation.NONE);
   }

}
