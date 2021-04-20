package maquette.core.server.commands.sandboxes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.TableResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ListSandboxesCommand implements Command {

   String project;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return services
         .getSandboxServices()
         .getSandboxes(user, project)
         .thenApply(sandboxes -> {
            var table = Table
               .create()
               .addColumns(StringColumn.create("name"))
               .addColumns(StringColumn.create("owner"))
               .addColumns(DateTimeColumn.create("created"))
               .addColumns(StringColumn.create("stacks"))
               .addColumns(StringColumn.create("id"));

            sandboxes.forEach(s -> {
               var row = table.appendRow();
               row.setString("name", s.getName());
               row.setString("owner", s.getCreated().getBy());
               row.setDateTime("created", LocalDateTime.ofInstant(s.getCreated().getAt(), ZoneId.systemDefault()));
               row.setString("stacks", s.getStacks().stream().map(st -> st.getConfiguration().getStackName()).collect(Collectors.joining(", ")));
               row.setString("id", s.getId().getValue());
            });

            return TableResult.apply(table.sortOn("name"), sandboxes);
         });
   }

   @Override
   public Command example() {
      return ListSandboxesCommand.apply("some-project");
   }

}
