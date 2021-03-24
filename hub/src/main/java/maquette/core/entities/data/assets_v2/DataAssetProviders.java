package maquette.core.entities.data.assets_v2;

import lombok.AllArgsConstructor;
import maquette.core.entities.data.assets_v2.exceptions.UnknownDataAssetTypeException;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetProviders {

   private final Map<String, DataAssetProvider> providers;

   public DataAssetProvider getByName(String type) {
      if (providers.containsKey(type)) {
         return providers.get(type);
      } else {
         throw UnknownDataAssetTypeException.apply(type);
      }
   }

   @SuppressWarnings("unchecked")
   public <T extends DataAssetProvider> T getByType(Class<T> type) {
      return providers
         .values()
         .stream()
         .filter(type::isInstance)
         .map(p -> (T) p)
         .findFirst()
         .orElseThrow();
   }

}
