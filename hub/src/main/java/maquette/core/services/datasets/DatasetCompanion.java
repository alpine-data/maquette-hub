package maquette.core.services.datasets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.data.datasets.DatasetEntity;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.exceptions.ProjectNotFoundException;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DatasetCompanion extends ServiceCompanion {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DatasetServices.class);

   private final ProjectEntities projects;

   private final DatasetEntities datasets;

   /**
    * Returns the pass through if the user is member of the origin project and the project is allowed to access the data.
    *
    * @param user        The user who is trying to access the data.
    * @param dataset     The name of the dataset.
    * @param project     The project which might have access to the dataset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String dataset, String project, T passThrough) {
      var requestsCS = datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.accessRequests().getDataAccessRequests());

      var prCS = projects
         .getProjectByName(project);

      var isMemberCS = prCS
         .thenCompose(p -> p.isMember(user));

      return Operators.compose(requestsCS, prCS, isMemberCS, (requests, pr, isMember) -> {
         var request = requests
            .stream()
            .filter(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED))
            .anyMatch(r -> r.getProject().equals(pr.getId()));

         if (request && isMember) {
            return Optional.of(passThrough);
         } else {
            return Optional.empty();
         }
      });
   }

   /**
    * Returns the pass through if the user is member of any project which is allowed to access the data asset.
    *
    * @param user        The user who is trying to access the data.
    * @param dataset     The name of the dataset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String dataset, T passThrough) {
      var requestsCS = datasets
         .getDatasetByName(dataset)
         .thenCompose(ds -> ds.accessRequests().getDataAccessRequests());
      var projectsCS = projects.getProjectsByMember(user);

      return Operators.compose(requestsCS, projectsCS, (requests, userProjects) -> {
         var request = requests
            .stream()
            .filter(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED))
            .anyMatch(r -> userProjects
               .stream()
               .anyMatch(p -> p.getId().equals(r.getProject())));

         if (request) {
            return Optional.of(passThrough);
         } else {
            return Optional.empty();
         }
      });
   }

   public <T> CompletionStage<Optional<T>> filterMember(User user, String dataset, DataAssetMemberRole role, T passThrough) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(d -> d.members().getMembers())
         .thenApply(members -> {
            var isOwner = members
               .stream()
               .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || role.equals(granted.getRole())));

            if (isOwner) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         });
   }

   public <T> CompletionStage<Optional<T>> filterRequester(User user, String dataset, UID accessRequest, T passThrough) {
      return
         datasets
         .getDatasetByName(dataset)
         .thenCompose(d -> d.accessRequests().getDataAccessRequestById(accessRequest))
         .thenCompose(r -> projects.getProjectById(r.getProject()))
         .thenCompose(p -> p.isMember(user))
         .thenApply(isMember -> {
            if (isMember) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         });
   }

   public <T> CompletionStage<Optional<T>> filterVisible(String dataset, T passThrough) {
      return datasets
         .getDatasetByName(dataset)
         .thenCompose(d -> d.getProperties().thenApply(properties -> {
            if (properties.getVisibility().equals(DataVisibility.PUBLIC)) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         }));
   }

   public CompletionStage<DataAccessRequest> enrichDataAccessRequest(DataAssetProperties dataset, DataAccessRequestProperties request) {
      return projects
         .getProjectById(request.getProject())
         .thenCompose(ProjectEntity::getProperties)
         .thenApply(project -> DataAccessRequest.apply(
            request.getId(),
            request.getCreated(),
            dataset, project, request.getEvents()));
   }

   public CompletionStage<Boolean> isSubscribedConsumer(User user, String dataset, String project) {
      return filterSubscribedConsumer(user, dataset, project, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isSubscribedConsumer(User user, String dataset) {
      return filterSubscribedConsumer(user, dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isMember(User user, String dataset) {
      return isMember(user, dataset, null);
   }

   public CompletionStage<Boolean> isMember(User user, String dataset, DataAssetMemberRole role) {
      return filterMember(user, dataset, role, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isRequester(User user, String dataset, UID accessRequest) {
      return filterRequester(user, dataset, accessRequest, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isVisible(String dataset) {
      return filterVisible(dataset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Dataset> mapEntityToDataset(DatasetEntity dataset) {
      return dataset
         .getProperties()
         .thenCompose(properties -> {
            var membersCS = dataset
               .members()
               .getMembers()
               .thenApply(members -> members
               .stream()
               .sorted(Comparator.comparing(granted -> granted.getAuthorization().getName()))
               .collect(Collectors.toList()));

            var versionsCS = dataset.revisions().getVersions();

            var accessRequestsCS = dataset
               .accessRequests()
               .getDataAccessRequests()
               .thenCompose(requests -> Operators.allOf(
                  requests
                     .stream()
                     .map(request -> enrichDataAccessRequest(properties, request))));

            return Operators
               .compose(
                  membersCS, accessRequestsCS,versionsCS,
                  (members, requests, versions) -> Dataset.apply(
                     dataset.getId(), properties.getTitle(), properties.getName(), properties.getSummary(),
                     properties.getVisibility(), properties.getClassification(), properties.getPersonalInformation(),
                     properties.getCreated(), properties.getUpdated(),
                     members, requests, List.of(), versions));
         });
   }

   public <T> CompletionStage<T> withProjectByName(String projectName, Function<ProjectEntity, CompletionStage<T>> func) {
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

}
