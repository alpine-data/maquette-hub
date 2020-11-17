package maquette.core.server.commands.sandboxes;

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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GetSandboxCommand implements Command {

   String project;

   String sandbox;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(sandbox) || sandbox.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`sandbox` must be supplied"));
      }

      return services
         .getSandboxServices()
         .getSandbox(user, project, sandbox)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return GetSandboxCommand.apply("my-funny-project", "laughing-edgar");
   }

}
