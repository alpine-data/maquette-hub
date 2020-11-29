package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class DatasetAlreadyExistsException extends RuntimeException implements DomainException {

   private DatasetAlreadyExistsException(String message) {
      super(message);
   }

   public static DatasetAlreadyExistsException withName(String datasetName) {
      var msg = String.format("Dataset `%s` already exists.", datasetName);
      return new DatasetAlreadyExistsException(msg);
   }

}
