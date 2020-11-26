package maquette.core.services.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.Dataset;
import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestDetails;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.exceptions.DatasetNotFoundException;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class DatasetCompanion extends ServiceCompanion {

   private static final Logger LOG = LoggerFactory.getLogger(DatasetServices.class);

   private final Projects projects;

   private final Datasets datasets;

   /**
    * Returns the pass through if the user is member of the origin project and the project is allowed to access the data.
    *
    * @param user        The user who is trying to access the data.
    * @param origin      The origin project name for which the user is trying to work with the data.
    * @param project     The project name which owns the dataset.
    * @param dataset     The name of the dataset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterConsumer(User user, String origin, String project, String dataset, T passThrough) {
      return withDatasetByName(project, dataset, (p, d) -> Operators.compose(
         projects.getProjectByName(origin).thenCompose(Project::getDetails), d.getDatasetDetails(),
         (originProject, datasetDetails) -> {
            var request = datasetDetails
               .getAccessRequests()
               .stream()
               .filter(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED))
               .filter(r -> r.getOriginProjectId().equals(originProject.getId()))
               .findAny();

            var member = originProject
               .getAuthorizations()
               .stream()
               .filter(auth -> auth.getAuthorization().isAuthorized(user))
               .findAny();

            if (request.isPresent() && member.isPresent()) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         }));
   }

   /**
    * Returns the pass through if the user is member of any project which is allowed to access the data asset.
    *
    * @param user        The user who is trying to access the data.
    * @param project     The project name which owns the dataset.
    * @param dataset     The name of the dataset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterConsumer(User user, String project, String dataset, T passThrough) {
      return withDatasetByName(project, dataset, (p, d) -> Operators.compose(
         projects.getProjectsByMember(user), d.getDatasetDetails(),
         (userProjects, datasetDetails) -> {
            var request = datasetDetails
               .getAccessRequests()
               .stream()
               .filter(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED))
               .anyMatch(r -> userProjects
                  .stream()
                  .anyMatch(pr -> pr.getId().equals(r.getOriginProjectId())));

            if (request) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         }));
   }

   public <T> CompletionStage<Optional<T>> filterConsumer(String token, String project, String dataset, T passThrough) {
      // TODO
      return CompletableFuture.completedFuture(Optional.empty());
   }

   public <T> CompletionStage<Optional<T>> filterOwner(User user, String project, String dataset, T passThrough) {
      return withDatasetByName(project, dataset, (p, d) -> d.getDatasetDetails().thenApply(datasetDetails -> {
         var isDataOwner = datasetDetails.isOwner(user);

         if (isDataOwner) {
            return Optional.of(passThrough);
         } else {
            return Optional.empty();
         }
      }));
   }

   public <T> CompletionStage<Optional<T>> filterVisible(String project, String dataset, T passThrough) {
      return withDatasetByName(project, dataset, (p, d) -> d.getDatasetDetails().thenApply(properties -> {
         if (properties.getVisibility().equals(DataVisibility.PUBLIC)) {
            return Optional.of(passThrough);
         } else {
            return Optional.empty();
         }
      }));
   }

   public CompletionStage<Boolean> isConsumer(User user, String origin, String project, String dataset) {
      return filterConsumer(user, origin, project, dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isConsumer(User user, String project, String dataset) {
      return filterConsumer(user, project, dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isConsumer(String token, String project, String dataset) {
      return filterConsumer(token, project, dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isOwner(User user, String project, String dataset) {
      return filterOwner(user, project, dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isVisible(String project, String dataset) {
      return filterVisible(project, dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Optional<DataAccessRequestDetails>> mapDataAccessRequestToDetails(Project targetProject, Dataset targetDataset, DataAccessRequest request) {
      return projects
         .findProjectById(request.getOriginProjectId())
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               var originPropertiesCS = maybeProject.get().getProperties();
               var targetDatasetPropertiesCS = targetDataset.getProperties();
               var targetProjectPropertiesCS = targetProject.getProperties();

               return Operators
                  .compose(
                     originPropertiesCS, targetDatasetPropertiesCS, targetProjectPropertiesCS,
                     (originProperties, targetDatasetProperties, targetProjectProperties) -> DataAccessRequestDetails.apply(
                        request.getId(),
                        request.getCreated(),
                        targetProjectProperties,
                        targetDatasetProperties,
                        originProperties,
                        request.getEvents(),
                        request.getStatus(),
                        true,
                        true
                     ))
                  .thenApply(Optional::of);
            } else {
               LOG.warn("Data Access Request {} is linked to non-existing project.", request);
               return CompletableFuture.completedFuture(Optional.empty());
            }
         });
   }

   public <T> CompletionStage<T> withProjectByName(String projectName, Function<Project, CompletionStage<T>> func) {
      return projects
         .findProjectByName(projectName)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               return func.apply(maybeProject.get());
            } else {
               throw ProjectNotFoundException.applyFromName(projectName);
            }
         });
   }

   public <T> CompletionStage<T> withDatasetByName(String projectName, String datasetName, BiFunction<Project, Dataset, CompletionStage<T>> func) {
      return withProjectByName(projectName, project -> datasets
         .findDatasetByName(project.getId(), datasetName)
         .thenCompose(maybeDataset -> {
            if (maybeDataset.isPresent()) {
               return func.apply(project, maybeDataset.get());
            } else {
               throw DatasetNotFoundException.applyFromName(projectName, datasetName);
            }
         }));
   }

}
