package maquette.adapters.datasets;

import maquette.core.ports.DatasetsStore;
import org.apache.commons.lang.NotImplementedException;

public final class DatasetsStores {

   private DatasetsStores() {

   }

   public static DatasetsStore create() {
      var config = DatasetsStoreConfiguration.apply();

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemDatasetsStore.apply(config.getFs());

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}
