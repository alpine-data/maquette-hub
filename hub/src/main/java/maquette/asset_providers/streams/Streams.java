package maquette.asset_providers.streams;

import maquette.core.entities.data.AbstractDataAssetProvider;
import maquette.core.entities.data.model.DataAssetProperties;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class Streams extends AbstractDataAssetProvider {

   public static final String TYPE_NAME = "stream";

   public Streams() {
      super(TYPE_NAME, StreamProperties.class);
   }

   public static Streams apply() {
      return new Streams();
   }

   @Override
   public CompletionStage<?> getDetails(DataAssetProperties properties, Object customProperties) {
      return CompletableFuture.completedFuture(customProperties);
   }

}
