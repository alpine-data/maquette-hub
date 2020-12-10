package maquette.core.services.data.datasources;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.datasources.DataSourceEntities;
import maquette.core.entities.data.datasources.DataSourceEntity;
import maquette.core.entities.data.datasources.model.*;
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
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceServicesImpl implements DataSourceServices {

   private final DataSourceEntities entities;

   private final DataAssetServices<DataSourceProperties, DataSourceEntity> assets;

   private final DataSourceCompanion comp;

   @Override
   public CompletionStage<DataSourceProperties> create(
      User executor, String title, String name, String summary,
      DataSourceDatabaseProperties properties, DataSourceType type, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return entities.create(
         executor, title, name, summary,
         properties, type, visibility, classification, personalInformation);
   }

   @Override
   public CompletionStage<DataSource> get(User executor, String dataSource) {
      return assets.get(executor, dataSource, comp::mapEntityToDataSource);
   }

   @Override
   public CompletionStage<List<DataSourceProperties>> list(User executor) {
      return assets.list(executor);
   }

   @Override
   public CompletionStage<Done> remove(User executor, String dataSource) {
      return assets.remove(executor, dataSource);
   }

   @Override
   public CompletionStage<Done> update(User executor, String name, String updatedName, String title, String summary, DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {
      return entities
         .getByName(name)
         .thenCompose(ds -> ds.update(executor, updatedName, title, summary, visibility, classification, personalInformation));
   }

   @Override
   public CompletionStage<Done> updateDatabaseProperties(User executor, String dataSource, DataSourceDriver driver, String connection, String query) {
      return entities
         .getByName(dataSource)
         .thenCompose(ds -> ds.update(executor, driver, connection, query));
   }

   @Override
   public CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role) {
      return assets.grant(executor, asset, member, role);
   }

   @Override
   public CompletionStage<Done> revoke(User executor, String asset, Authorization member) {
      return assets.revoke(executor, asset, member);
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

}
