package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CreateDataAccessRequestView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.data.DataClassification;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDataAccessRequestViewCommand implements Command {

   String project;

   String asset;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`project` must be supplied"));
      } else if (Objects.isNull(asset) || asset.length() == 0) {
         return CompletableFuture.failedFuture(new RuntimeException("`asset` must be supplied"));
      }

      var projectCS = services
         .getProjectServices()
         .get(user, project);

      var datasetCS = services
         .getDatasetServices()
         .getDataset(user, project, asset);

      var projectsCS = services
         .getUserServices()
         .getProjects(user);

      return Operators.compose(projectCS, datasetCS, projectsCS, (project, dataset, projects) -> {
         var existingRequests = dataset
            .getAccessRequests()
            .stream()
            .map(DataAccessRequest::getOriginProjectId)
            .collect(Collectors.toList());

         var asset = DatasetProperties.apply(
            dataset.getId(), dataset.getTitle(), dataset.getName(), dataset.getSummary(), dataset.getDescription(),
            dataset.getVisibility(), dataset.getClassification(), dataset.getPersonalInformation(), dataset.getCreated(),
            dataset.getUpdated());

         var availableProjects = projects
            .stream()
            .filter(p -> existingRequests.stream().noneMatch(id -> p.getId().equals(id)))
            .filter(p -> !p.getId().equals(project.getId()))
            .collect(Collectors.toList());

         var isOwner = dataset.isOwner(user);
         var isSubscribe = dataset.getClassification().equals(DataClassification.PUBLIC);

         return CreateDataAccessRequestView.apply(project, asset, availableProjects, isOwner, isSubscribe);
      });
   }

   @Override
   public Command example() {
      return null;
   }

}
