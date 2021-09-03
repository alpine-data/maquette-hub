package maquette.datashop.providers;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.datashop.exceptions.UnknownDataAssetTypeException;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public final class DataAssetProviders {

   private final Map<String, DataAssetProvider> providers;

   public static DataAssetProviders apply(DataAssetProvider...providers) {
      var map = Maps.<String, DataAssetProvider>newHashMap();

      for (var p : providers) {
         map.put(p.getType(), p);
      }

      return apply(map);
   }

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

   public Map<String, DataAssetProvider> toMap() {
      return Map.copyOf(providers);
   }
}
