package maquette.core.server.commands.data_v2;

import lombok.AllArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ListDataAssetsCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {

      return services
         .getDataAssetServices()
         .list(user)
         .thenApply(datasets -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("type"))
               .addColumns(StringColumn.create("name"))
               .addColumns(StringColumn.create("visibility"))
               .addColumns(StringColumn.create("classification"))
               .addColumns(StringColumn.create("personal information"));

            datasets.forEach(p -> {
               var row = table.appendRow();
               row.setString("type", p.getType());
               row.setString("name", p.getMetadata().getName());
               row.setString("visibility", p.getMetadata().getVisibility().getValue());
               row.setString("classification", p.getMetadata().getClassification().getValue());
               row.setString("personal information", p.getMetadata().getPersonalInformation().getValue());
            });

            return TableResult.apply(table.sortOn("name"), datasets);
         });
   }

   @Override
   public Command example() {
      return ListDataAssetsCommand.apply();
   }

}
