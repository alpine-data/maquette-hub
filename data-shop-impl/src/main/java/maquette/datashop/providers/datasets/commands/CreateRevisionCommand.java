package maquette.datashop.providers.datasets.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.server.commands.CommandResult;
import maquette.core.server.commands.DataResult;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.providers.datasets.Datasets;
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
   public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
      return runtime
          .getModule(MaquetteDataShop.class)
          .getProviders()
          .getByType(Datasets.class)
          .getServices()
          .create(user, dataset, schema)
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
