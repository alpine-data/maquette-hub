package maquette.core.server.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.DataResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.Authorizations;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDatasetDataAccessRequestCommand implements Command {


   String project;

   String dataset;

   String authorizationType;

   String authorizationValue;

   String reason;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(dataset) || dataset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`dataset` must be supplied"));
      } else if (Objects.isNull(authorizationType)) {
         return CompletableFuture.failedFuture(new RuntimeException("`authorization-type` must be supplied"));
      }

      // TODO mw: Better validation process

      return services
         .getDatasetServices()
         .createDataAccessRequest(user, project, dataset, Authorizations.fromString(authorizationType, authorizationValue), reason)
         .thenApply(DataResult::apply);
   }

   @Override
   public Command example() {
      return CreateDatasetDataAccessRequestCommand.apply("my-funny-project", "my-funny-dataset", "user", "egon", "Because he wants to.");
   }
}
