package maquette.core.entities.datasets.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class DatasetNotFoundException extends RuntimeException implements DomainException {

   private DatasetNotFoundException(String message) {
      super(message);
   }

   public static DatasetNotFoundException apply(String datasetName) {
      var msg = String.format("Dataset `%s` does not found", datasetName);
      return new DatasetNotFoundException(msg);
   }

}
