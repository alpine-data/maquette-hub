package maquette.core.entities.data.datasources.exceptions;

import maquette.core.entities.data.datasources.model.FailedConnectionTestResult;
import maquette.core.values.exceptions.DomainException;

public final class DataSourceFetchException extends RuntimeException implements DomainException {

   private DataSourceFetchException(String message) {
      super(message);
   }

   public static DataSourceFetchException apply(FailedConnectionTestResult result) {
      var msg = String.format("Unable to read data from database with configured parameters. Reason: %s", result.getMessage());
      return new DataSourceFetchException(msg);
   }

}
