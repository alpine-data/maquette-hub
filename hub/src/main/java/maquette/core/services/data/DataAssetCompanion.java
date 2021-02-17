package maquette.core.services.data;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.*;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataAssetCompanion<P extends DataAssetProperties<P>, E extends DataAssetEntities<P, ?>> extends ServiceCompanion {

   private static final Logger LOG = LoggerFactory.getLogger(ServiceCompanion.class);

   private final E assets;

   private final ProjectEntities projects;

   public static <P extends DataAssetProperties<P>, E extends DataAssetEntities<P, ?>> DataAssetCompanion<P, E> apply(E assets, ProjectEntities projects) {
      return new DataAssetCompanion<>(assets, projects);
   }

   public CompletionStage<DataAccessRequest> enrichDataAccessRequest(P asset, DataAccessRequestProperties request) {
      return projects
         .getProjectById(request.getProject())
         .thenCompose(ProjectEntity::getProperties)
         .thenApply(project -> DataAccessRequest.apply(
            request.getId(),
            request.getCreated(),
            asset, project, request.getEvents()));
   }

   /**
    * Returns the pass through if the user is member of the origin project and the project is allowed to access the data.
    *
    * @param user        The user who is trying to access the data.
    * @param asset       The name of the asset.
    * @param project     The project which might have access to the asset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String asset, UID project, T passThrough) {
      var requestsCS = assets
         .getByName(asset)
         .thenCompose(ds -> ds.getAccessRequests().getDataAccessRequests());

      var prCS = projects.getProjectById(project);

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
    * @param asset       The name of the asset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String asset, T passThrough) {
      var requestsCS = assets
         .getByName(asset)
         .thenCompose(ds -> ds.getAccessRequests().getDataAccessRequests());

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

   public <T> CompletionStage<Optional<T>> filterPermission(User user, String asset, Function<DataAssetPermissions, Boolean> check, T passThrough) {
      var entityCS = assets.getByName(asset);
      var propertiesCS = entityCS.thenCompose(DataAssetEntity::getProperties);
      var accessRequestsCS = Operators.compose(entityCS, propertiesCS, (entity, properties) -> entity
         .getAccessRequests()
         .getDataAccessRequests()
         .thenCompose(requests -> Operators.allOf(requests
            .stream()
            .map(request -> enrichDataAccessRequest(properties, request)))))
         .thenCompose(cs -> cs);
      var membersListCS = entityCS.thenCompose(entity -> entity.getMembers().getMembers());

      return Operators.compose(accessRequestsCS, membersListCS, (accessRequests, membersList) -> {
         var members = GenericDataAssetMembers.apply(membersList, accessRequests);
         var result = check.apply(members.getDataAssetPermissions(user));

         if (result) {
            return Optional.of(passThrough);
         } else {
            return Optional.empty();
         }
      });
   }

   public <T> CompletionStage<Optional<T>> filterMember(User user, String asset, T passThrough) {
      return filterMember(user, asset, null, passThrough);
   }

   public <T> CompletionStage<Optional<T>> filterMember(User user, String asset, DataAssetMemberRole role, T passThrough) {
      return assets
         .getByName(asset)
         .thenCompose(d -> d.getMembers().getMembers())
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

   public <T> CompletionStage<Optional<T>> filterRequester(User user, String asset, UID accessRequest, T passThrough) {
      return assets
         .getByName(asset)
         .thenCompose(d -> d.getAccessRequests().getDataAccessRequestById(accessRequest))
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

   public <T> CompletionStage<Optional<T>> filterVisible(String asset, T passThrough) {
      return assets
         .getByName(asset)
         .thenCompose(d -> d.getProperties().thenApply(properties -> {
            if (properties.getVisibility().equals(DataVisibility.PUBLIC)) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         }));
   }

   public CompletionStage<Boolean> hasPermission(User user, String asset, Function<DataAssetPermissions, Boolean> check) {
      return filterPermission(user, asset, check, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isSubscribedConsumer(User user, String asset, UID project) {
      return filterSubscribedConsumer(user, asset, project, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isSubscribedConsumer(User user, String asset) {
      return filterSubscribedConsumer(user, asset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isMember(User user, String asset) {
      return isMember(user, asset, null);
   }

   public CompletionStage<Boolean> isMember(User user, String asset, DataAssetMemberRole role) {
      return filterMember(user, asset, role, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isRequester(User user, String asset, UID accessRequest) {
      return filterRequester(user, asset, accessRequest, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isVisible(String asset) {
      return filterVisible(asset, Done.getInstance()).thenApply(Optional::isPresent);
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class GenericDataAssetMembers implements DataAssetMembers {

      List<GrantedAuthorization<DataAssetMemberRole>> members;

      List<DataAccessRequest> accessRequests;

   }

}
