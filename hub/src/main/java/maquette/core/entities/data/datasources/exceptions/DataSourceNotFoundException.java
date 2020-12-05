package maquette.core.entities.data.datasources.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class DataSourceNotFoundException extends RuntimeException implements DomainException {

   private DataSourceNotFoundException(String message) {
      super(message);
   }

   public static DataSourceNotFoundException withName(String datasetName) {
      var msg = String.format("Data source `%s` does not exist.", datasetName);
      return new DataSourceNotFoundException(msg);
   }

   public static DataSourceNotFoundException withId(UID id) {
      var msg = String.format("Data source with id `%s` not found.", id);
      return new DataSourceNotFoundException(msg);
   }

}
