package maquette.core.server.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDatasetCommand implements Command {

   String project;

   String title;

   String name;

   String summary;

   String visibility;

   String classification;

   String personalInformation;

   String description;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(title) ||title.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`title` must be supplied"));
      } else if (Objects.isNull(name) ||name.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`name` must be supplied"));
      } else if (Objects.isNull(visibility) ||visibility.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`visibility` must be supplied"));
      } else if (Objects.isNull(classification) ||classification.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`classification` must be supplied"));
      } else if (Objects.isNull(personalInformation) ||personalInformation.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`personalInformation` must be supplied"));
      }

      // TODO mw: Better validation process
      var visibilityMapped = DataVisibility.forValue(visibility);
      var classificationMapped = DataClassification.forValue(classification);
      var personalInformationMapped = PersonalInformation.forValue(personalInformation);

      return services
         .getDatasetServices()
         .createDataset(user, project, title, name, summary, description, visibilityMapped, classificationMapped, personalInformationMapped)
         .thenApply(pid -> MessageResult.apply("Successfully created dataset %s/%s", project, name));
   }

   @Override
   public Command example() {
      return CreateDatasetCommand.apply(
         "my-funny-project", "Funny Dataset", "my-funny-dataset",
         "Lorem ipsum", "public", "public", "none", "Lorem ipsum");
   }
}
