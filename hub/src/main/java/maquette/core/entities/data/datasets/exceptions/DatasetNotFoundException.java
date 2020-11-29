package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class DatasetNotFoundException extends RuntimeException implements DomainException {

   private DatasetNotFoundException(String message) {
      super(message);
   }

   public static DatasetNotFoundException withName(String datasetName) {
      var msg = String.format("Dataset `%s` is not found", datasetName);
      return new DatasetNotFoundException(msg);
   }

   public static DatasetNotFoundException withId(UID id) {
      var msg = String.format("Dataset with id `%s` not found.", id);
      return new DatasetNotFoundException(msg);
   }

}
