package maquette.core.services.data.streams;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.data.streams.StreamEntity;
import maquette.core.entities.data.streams.model.Retention;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.services.data.DataAssetServices;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class StreamServicesImpl implements StreamServices {

   private final StreamEntities entities;

   private final DataAssetServices<StreamProperties, StreamEntity> assets;

   private final StreamCompanion comp;

   @Override
   public CompletionStage<StreamProperties> create(
      User executor, String title, String name, String summary, Retention retention, Schema schema,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return entities.create(executor, title, name, summary, retention, schema, visibility, classification, personalInformation);
   }

   @Override
   public CompletionStage<Stream> get(User executor, String asset) {
      return assets.get(executor, asset, comp::mapEntityToAsset);
   }

   @Override
   public CompletionStage<List<StreamProperties>> list(User executor) {
      return assets.list(executor);
   }

   @Override
   public CompletionStage<Done> remove(User executor, String asset) {
      return assets.remove(executor, asset);
   }

   @Override
   public CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary, Retention retention, Schema schema,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return entities
         .getByName(name)
         .thenCompose(as -> as.update(executor, name, title, summary, retention, schema, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason) {
      return assets.createDataAccessRequest(executor, asset, project, reason);
   }

   @Override
   public CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request) {
      return assets.getDataAccessRequest(executor, asset, request);
   }

   @Override
   public CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message) {
      return assets.grantDataAccessRequest(executor, asset, request, until, message);
   }

   @Override
   public CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason) {
      return assets.rejectDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason) {
      return assets.updateDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason) {
      return assets.withdrawDataAccessRequest(executor, asset, request, reason);
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return assets.grant(executor, asset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return assets.revoke(executor, asset, member);
   }

}
