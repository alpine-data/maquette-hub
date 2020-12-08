package maquette.core.entities.data.streams;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.data.DataAssetEntities;
import maquette.core.entities.data.datasources.model.DataSourceDatabaseProperties;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.entities.data.datasources.model.DataSourceType;
import maquette.core.entities.data.streams.exceptions.StreamAlreadyExistsException;
import maquette.core.entities.data.streams.exceptions.StreamNotFoundException;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.ports.StreamsRepository;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class StreamEntities implements DataAssetEntities<StreamProperties, StreamEntity> {

   private final StreamsRepository repository;

   public CompletionStage<StreamProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation) {

      return repository
      .findAssetByName(name)
      .thenCompose(maybeStream -> {
         if (maybeStream.isPresent()) {
            return CompletableFuture.failedFuture(StreamAlreadyExistsException.withName(name));
         } else {
            var created = ActionMetadata.apply(executor);
            var stream = StreamProperties.apply(
               UID.apply(), title, name, summary,
               visibility, classification, personalInformation, created, created);

            return repository
               .insertOrUpdateAsset(stream)
               .thenApply(d -> stream);
         }
      });
   }

   @Override
   public CompletionStage<List<DataAccessRequestProperties>> findAccessRequestsByProject(UID project) {
      return repository.findDataAccessRequestsByProject(project);
   }

   @Override
   public CompletionStage<Optional<StreamEntity>> findById(UID asset) {
      return repository
         .findAssetById(asset)
         .thenApply(maybeAsset -> maybeAsset.map(properties -> StreamEntity.apply(properties.getId(), repository)));
   }

   @Override
   public CompletionStage<Optional<StreamEntity>> findByName(String asset) {
      return repository
         .findAssetByName(asset)
         .thenApply(maybeAsset -> maybeAsset.map(properties -> StreamEntity.apply(properties.getId(), repository)));
   }

   @Override
   public CompletionStage<StreamEntity> getById(UID asset) {
      return findById(asset).thenApply(opt -> opt.orElseThrow(() -> StreamNotFoundException.withId(asset)));
   }

   @Override
   public CompletionStage<StreamEntity> getByName(String asset) {
      return findByName(asset).thenApply(opt -> opt.orElseThrow(() -> StreamNotFoundException.withName(asset)));
   }

   @Override
   public CompletionStage<List<StreamProperties>> list() {
      return repository.findAllAssets();
   }

   @Override
   public CompletionStage<Done> remove(UID asset) {
      throw new NotImplementedException();
   }

}
