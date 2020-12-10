package maquette.core.server.commands.data.streams;

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
public final class ListStreamsCommand implements Command {

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {

      return services
         .getStreamServices()
         .list(user)
         .thenApply(dataSources -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("title"))
               .addColumns(StringColumn.create("name"))
               .addColumns(StringColumn.create("visibility"))
               .addColumns(StringColumn.create("classification"))
               .addColumns(StringColumn.create("personal information"));

            dataSources.forEach(p -> {
               var row = table.appendRow();
               row.setString("title", p.getTitle());
               row.setString("name", p.getName());
               row.setString("visibility", p.getVisibility().getValue());
               row.setString("classification", p.getClassification().getValue());
               row.setString("personal information", p.getPersonalInformation().getValue());
            });

            return TableResult.apply(table.sortOn("name"), dataSources);
         });
   }

   @Override
   public Command example() {
      return apply();
   }

}
