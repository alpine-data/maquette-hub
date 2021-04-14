package maquette.core.services.data;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.data.DataAssetProviders;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.services.ServiceCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetMembers;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetCompanion extends ServiceCompanion {

   private final DataAssetEntities entities;

   private final ProjectEntities projects;

   private final DataAssetProviders providers;

   public CompletionStage<DataAsset> enrichDataAsset(DataAssetEntity entity) {
      var propertiesCS = entity.getProperties();
      var membersCS = entity.getMembers().getMembers();

      var accessRequestsRawCS = entity
         .getAccessRequests()
         .getDataAccessRequests();

      var accessRequestsCS = Operators
         .compose(propertiesCS, accessRequestsRawCS, (properties, accessRequestsRaw) -> accessRequestsRaw
            .stream()
            .map(request -> this.enrichDataAccessRequest(properties, request)))
         .thenCompose(Operators::allOf);

      var customSettingsCS = propertiesCS.thenCompose(properties -> {
         var provider = providers.getByName(properties.getType());
         return entity.getCustomSettings(provider.getSettingsType());
      });

      var customDetailsCS = Operators
         .compose(propertiesCS, customSettingsCS, (properties, customSettings) -> providers
            .getByName(properties.getType())
            .getDetails(properties, customSettings))
         .thenCompose(cs -> cs);

      return Operators.compose(
         propertiesCS, accessRequestsCS, membersCS, customSettingsCS, customDetailsCS, DataAsset::apply);
   }

   public CompletionStage<DataAccessRequest> enrichDataAccessRequest(DataAssetProperties properties, DataAccessRequestProperties request) {
      return projects
         .getProjectById(request.getProject())
         .thenCompose(ProjectEntity::getProperties)
         .thenApply(project -> DataAccessRequest.apply(
            request.getId(),
            request.getCreated(),
            properties,
            project,
            request.getEvents()));
   }

   /**
    * Returns the pass through if the user is member of the origin project and the project is allowed to access the data.
    *
    * @param user        The user who is trying to access the data.
    * @param name       The name of the asset.
    * @param project     The project which might have access to the asset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String name, UID project, T passThrough) {
      var requestsCS = entities
         .getByName(name)
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
    * @param name       The name of the asset.
    * @param passThrough The pass through.
    * @param <T>         The type of the pass through.
    * @return The pass through or Optional.empty()
    */
   public <T> CompletionStage<Optional<T>> filterSubscribedConsumer(User user, String name, T passThrough) {
      var requestsCS = entities
         .getByName(name)
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

   public <T> CompletionStage<Optional<T>> filterPermission(User user, String name, Function<DataAssetPermissions, Boolean> check, T passThrough) {
      var entityCS = entities.getByName(name);
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

   public <T> CompletionStage<Optional<T>> filterMember(User user, String name, T passThrough) {
      return filterMember(user, name, null, passThrough);
   }

   public <T> CompletionStage<Optional<T>> filterMember(User user, String asset, DataAssetMemberRole role, T passThrough) {
      return entities
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

   public <T> CompletionStage<Optional<T>> filterRequester(User user, String name, UID accessRequest, T passThrough) {
      return entities
         .getByName(name)
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

   public <T> CompletionStage<Optional<T>> filterVisible(String name, T passThrough) {
      return entities
         .getByName(name)
         .thenCompose(d -> d.getProperties().thenApply(properties -> {
            if (properties.getMetadata().getVisibility().equals(DataVisibility.PUBLIC)) {
               return Optional.of(passThrough);
            } else {
               return Optional.empty();
            }
         }));
   }

   public CompletionStage<Boolean> hasPermission(User user, String name, Function<DataAssetPermissions, Boolean> check) {
      return filterPermission(user, name, check, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isSubscribedConsumer(User user, String name, UID project) {
      return filterSubscribedConsumer(user, name, project, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isSubscribedConsumer(User user, String name) {
      return filterSubscribedConsumer(user, name, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isMember(User user, String name) {
      return isMember(user, name, null);
   }

   public CompletionStage<Boolean> isMember(User user, String name, DataAssetMemberRole role) {
      return filterMember(user, name, role, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isRequester(User user, String name, UID accessRequest) {
      return filterRequester(user, name, accessRequest, Done.getInstance()).thenApply(Optional::isPresent);
   }

   public CompletionStage<Boolean> isVisible(String name) {
      return filterVisible(name, Done.getInstance()).thenApply(Optional::isPresent);
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class GenericDataAssetMembers implements DataAssetMembers {

      List<GrantedAuthorization<DataAssetMemberRole>> members;

      List<DataAccessRequest> accessRequests;

      public List<DataAccessRequest> getAccessRequests() {
         // TODO: Transform
         return List.of();
      }
   }

}
