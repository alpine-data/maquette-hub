package maquette.core.server.commands.datasets.tokens;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDatasetDataAccessTokenCommand implements Command {


   String project;

   String dataset;

   String origin;

   String token;

   String description;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      return CompletableFuture.completedFuture(MessageResult.apply("ok"));
   }

   @Override
   public Command example() {
      return CreateDatasetDataAccessTokenCommand.apply("some-project", "some-dataset", "some-origin", "some-token", Operators.lorem());
   }
}
