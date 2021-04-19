package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.services.projects.EnvironmentType;
import maquette.core.values.user.User;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetProjectEnvironmentCommand implements Command {

   String name;

   EnvironmentType type;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var environmentType = type != null ? type : EnvironmentType.EXTERNAL;

      return services
         .getProjectServices()
         .environment(user, name, environmentType)
         .thenApply(properties -> {
             var table = Table
                .create()
                .addColumns(StringColumn.create("key"))
                .addColumns(StringColumn.create("value"));

             properties.keySet().forEach(p -> {
                 var row = table.appendRow();
                 row.setString("key", p);
                 row.setString("value", properties.get(p));
             });

             return TableResult.apply(table.sortOn("key"), properties);
         });
   }

   @Override
   public Command example() {
      return apply("some-project", EnvironmentType.EXTERNAL);
   }

}
