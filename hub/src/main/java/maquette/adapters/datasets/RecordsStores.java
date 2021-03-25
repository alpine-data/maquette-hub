package maquette.adapters.datasets;

import maquette.config.RepositoryConfiguration;
import maquette.core.ports.RecordsStore;
import org.apache.commons.lang.NotImplementedException;

public final class RecordsStores {

   private RecordsStores() {

   }

   public static RecordsStore create() {
      var config = RepositoryConfiguration.apply("datasets-store");

      switch (config.getType()) {
         case "filesystem":
         case "fs":
         case "files":
            return FileSystemRecordsStore.apply(config.getFs());

         default:
         case "in-mem":
         case "in-memory":
            throw new NotImplementedException();
      }
   }

}
