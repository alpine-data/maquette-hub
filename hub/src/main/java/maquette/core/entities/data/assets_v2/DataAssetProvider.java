package maquette.core.entities.data.assets_v2;

import maquette.core.entities.data.assets_v2.model.DataAssetProperties;

import java.util.concurrent.CompletionStage;

public interface DataAssetProvider {

   Class<?> getPropertiesType();

   String getType();

   CompletionStage<Object> getDetails(DataAssetProperties properties, Object customProperties);

}
