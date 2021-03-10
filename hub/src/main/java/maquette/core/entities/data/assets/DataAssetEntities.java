package maquette.core.entities.data.assets;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DataAssetEntities<T extends DataAssetProperties<T>, E extends DataAssetEntity<T>> {

   CompletionStage<List<DataAccessRequestProperties>> findAccessRequestsByProject(UID project);

   CompletionStage<Optional<E>> findById(UID asset);

   CompletionStage<Optional<E>> findByName(String asset);

   CompletionStage<E> getById(UID asset);

   CompletionStage<E> getByName(String asset);

   CompletionStage<List<T>> list();

   CompletionStage<Done> remove(UID asset);

}
