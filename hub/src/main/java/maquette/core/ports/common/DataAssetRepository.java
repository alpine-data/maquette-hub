package maquette.core.ports.common;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DataAssetRepository<T extends DataAssetProperties> {

   CompletionStage<List<T>> findAllAssets();

   CompletionStage<Optional<T>> findAssetById(UID asset);

   CompletionStage<Optional<T>> findAssetByName(String name);

   CompletionStage<Done> insertOrUpdateAsset(T asset);

}
