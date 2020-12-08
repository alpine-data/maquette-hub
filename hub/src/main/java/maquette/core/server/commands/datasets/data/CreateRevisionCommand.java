package maquette.core.server.commands.datasets.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateRevisionCommand implements Command {

   String dataset;

   Schema schema;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getDatasetServices()
         .createRevision(user, dataset, schema)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return CreateRevisionCommand.apply(
         "some-dataset",
         SchemaBuilder
            .record("Test")
            .fields()
            .requiredLong("id")
            .requiredString("color")
            .optionalDouble("price")
            .endRecord());
   }
}
