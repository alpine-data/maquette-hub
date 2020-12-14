package maquette.core.entities.data.datasources;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.entities.data.datasets.AccessRequests;
import maquette.core.values.data.records.Records;
import maquette.core.entities.data.datasources.exceptions.DataSourceNotFoundException;
import maquette.core.entities.data.datasources.model.DataSourceAccessType;
import maquette.core.entities.data.datasources.model.DataSourceDatabaseProperties;
import maquette.core.entities.data.datasources.model.DataSourceDriver;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.ports.DataExplorer;
import maquette.core.ports.DataSourcesRepository;
import maquette.core.ports.JdbcPort;
import maquette.core.ports.RecordsStore;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceEntity implements DataAssetEntity<DataSourceProperties> {

   private final UID id;

   private final DataSourcesRepository repository;

   private final JdbcPort jdbcPort;

   private final RecordsStore recordsStore;

   private final DataExplorer explorer;

   public CompletionStage<Records> download(User executor) {
      return withProperties(props -> jdbcPort.read(props.getDatabase()));
   }

   public AccessRequests<DataSourceProperties> getAccessRequests() {
      return AccessRequests.apply(id, repository, this::getProperties);
   }

   public MembersCompanion<DataAssetMemberRole> getMembers() {
      return MembersCompanion.apply(id, repository);
   }

   public UID getId() {
      return id;
   }

   public CompletionStage<DataSourceProperties> getProperties() {
      return withProperties(CompletableFuture::completedFuture);
   }

   public CompletionStage<Done> update(
      User executor, String name, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      // TODO mw: value validation ...

      return withProperties(properties -> {
         var updated = properties
            .withName(name)
            .withTitle(title)
            .withSummary(summary)
            .withVisibility(visibility)
            .withClassification(classification)
            .withPersonalInformation(personalInformation)
            .withUpdated(ActionMetadata.apply(executor));

         return repository.insertOrUpdateAsset(updated);
      });
   }

   public CompletionStage<Done> update(
      User executor, DataSourceDriver driver, String connection, String username, String password, String query,
      DataSourceAccessType accessType) {

      return  withProperties(properties -> {
         var updated = properties
            .withDatabase(DataSourceDatabaseProperties.apply(
               driver, connection, query, username, password))
            .withAccessType(accessType)
            .withUpdated(ActionMetadata.apply(executor));

         return repository.insertOrUpdateAsset(updated);
      });
   }

   private <T> CompletionStage<T> withProperties(Function<DataSourceProperties, CompletionStage<T>> func) {
      return repository
         .findAssetById(id)
         .thenApply(opt -> opt.orElseThrow(() -> DataSourceNotFoundException.withId(id)))
         .thenCompose(func);
   }

}
