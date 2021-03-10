package maquette.core.entities.data.assets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetProperties;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataAssetCompanion<T extends DataAssetProperties<T>> {

   private final UID id;

   private final Supplier<CompletionStage<T>> getProperties;

   public static <T extends DataAssetProperties<T>> DataAssetCompanion<T> apply(
      UID id, Supplier<CompletionStage<T>> getProperties) {

      return new DataAssetCompanion<>(id, getProperties);
   }



}
