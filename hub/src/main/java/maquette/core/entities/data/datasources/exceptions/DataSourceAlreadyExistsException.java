package maquette.core.entities.data.datasources.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class DataSourceAlreadyExistsException extends RuntimeException implements DomainException {

   private DataSourceAlreadyExistsException(String message) {
      super(message);
   }

   public static DataSourceAlreadyExistsException withName(String dataSourceName) {
      var msg = String.format("Data source `%s` already exists.", dataSourceName);
      return new DataSourceAlreadyExistsException(msg);
   }

}
