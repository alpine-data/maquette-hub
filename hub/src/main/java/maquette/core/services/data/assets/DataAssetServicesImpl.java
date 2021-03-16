package maquette.core.services.data.assets;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.assets_v2.DataAssetEntities;
import maquette.core.entities.data.assets_v2.DataAssetEntity;
import maquette.core.entities.data.assets_v2.DataAssetProviders;
import maquette.core.entities.data.assets_v2.model.DataAsset;
import maquette.core.entities.data.assets_v2.model.DataAssetMetadata;
import maquette.core.entities.data.assets_v2.model.DataAssetProperties;
import maquette.core.entities.data.datasets.model.tasks.Task;
import maquette.core.entities.logs.LogEntry;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.logs.LogsCompanion;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.access.DataAccessRequestV2;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetServicesImpl implements DataAssetServices {

   private final DataAssetProviders providers;

   private final DataAssetEntities entities;

   private final ProjectEntities projects;

   private final DataAssetCompanion companion;

   private final LogsCompanion logs;

   @Override
   public CompletionStage<DataAssetProperties> create(User executor, String type, DataAssetMetadata metadata, Authorization owner, Authorization steward, @Nullable Object customProperties) {
      return entities.create(executor, type, metadata, owner, steward, customProperties);
   }

   @Override
   public CompletionStage<DataAsset> get(User executor, String name) {
      return entities
         .getByName(name)
         .thenCompose(entity -> {
            var propertiesCS = entity.getProperties();
            var membersCS = entity.getMembers().getMembers();

            var accessRequestsRawCS = entity
               .getAccessRequests()
               .getDataAccessRequests();

            var accessRequestsCS = Operators
               .compose(propertiesCS, accessRequestsRawCS, (properties, accessRequestsRaw) -> accessRequestsRaw
                  .stream()
                  .map(request -> companion.enrichDataAccessRequest(properties, request)))
               .thenCompose(Operators::allOf);

            var customPropertiesCS = propertiesCS.thenCompose(properties -> {
               var provider = providers.get(properties.getType());
               return entity.getCustomProperties(provider.getPropertiesType());
            });

            var customDetailsCS = Operators
               .compose(propertiesCS, customPropertiesCS, (properties, customProperties) -> providers
                  .get(properties.getType())
                  .getDetails(properties, customProperties))
               .thenCompose(cs -> cs);

            return Operators.compose(
               propertiesCS, accessRequestsCS, membersCS, customPropertiesCS, customDetailsCS, DataAsset::apply);
         });
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> list(User executor) {
      return entities.list();
   }

   @Override
   public CompletionStage<Done> approve(User executor, String name) {
      return entities.getByName(name).thenCompose(entity -> entity.approve(executor));
   }

   @Override
   public CompletionStage<Done> deprecate(User executor, String name, boolean deprecate) {
      return entities.getByName(name).thenCompose(entity -> entity.deprecate(executor, deprecate));
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, DataAssetMetadata metadata) {
      return entities.getByName(name).thenCompose(entity -> entity.update(executor, metadata));
   }

   @Override
   public CompletionStage<Done> updateCustomProperties(User executor, String name, Object customProperties) {
      return entities.getByName(name).thenCompose(entity -> entity.updateCustomProperties(executor, customProperties));
   }

   @Override
   public CompletionStage<Done> remove(User executor, String name) {
      return entities.removeByName(name);
   }

   @Override
   public CompletionStage<List<LogEntry>> getAccessLogs(User executor, String name) {
      return entities
         .getByName(name)
         .thenCompose(DataAssetEntity::getProperties)
         .thenApply(DataAssetProperties::getType)
         .thenApply(UID::apply)
         .thenCompose(logs::getLogsByResourcePrefix);
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String name, String project, String reason) {
      var entityCS = entities.getByName(name);
      var projectEntityCS = projects.getProjectByName(project);

      return Operators
         .compose(entityCS, projectEntityCS, (entity, projectEntity) -> entity
            .getAccessRequests()
            .createDataAccessRequest(executor, projectEntity.getId(), reason))
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<DataAccessRequestV2> getDataAccessRequest(User executor, String name, UID request) {
      var entityCS = entities.getByName(name);
      var propertiesCS = entityCS.thenCompose(DataAssetEntity::getProperties);
      var accessRequestPropertiesCS = entityCS.thenCompose(a -> a.getAccessRequests().getDataAccessRequestById(request));

      return Operators
         .compose(propertiesCS, accessRequestPropertiesCS, companion::enrichDataAccessRequest)
         .thenCompose(cs -> cs);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String name, UID request, @Nullable Instant until, @Nullable String message) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().grantDataAccessRequest(executor, request, until, message));
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String name, UID request, String reason) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().rejectDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String name, UID request, String reason) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().updateDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String name, UID request, @Nullable String reason) {
      return entities
         .getByName(name)
         .thenCompose(a -> a.getAccessRequests().withdrawDataAccessRequest(executor, request, reason));
   }

   @Override
   public CompletionStage<List<Task>> getNotifications(User executor, String name) {
      return null; // TODO
   }

   @Override
   public CompletionStage<List<Task>> getNotifications(User executor) {
      return null; // TODO
   }

   @Override
   public CompletionStage<Done> grant(User executor, String name, Authorization member, DataAssetMemberRole role) {
      return entities
         .getByName(name)
         .thenCompose(e -> e.getMembers().addMember(executor, member, role));
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String name, Authorization member) {
      return entities
         .getByName(name)
         .thenCompose(e -> e.getMembers().removeMember(executor, member));
   }

}
