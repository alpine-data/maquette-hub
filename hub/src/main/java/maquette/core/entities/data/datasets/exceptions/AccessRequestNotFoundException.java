package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class AccessRequestNotFoundException extends RuntimeException implements DomainException {

   private AccessRequestNotFoundException(String message) {
      super(message);
   }

   public static AccessRequestNotFoundException apply(String datasetId, String datasetName, String accessRequestId) {
      var msg = String.format(
         "Dataset `%s (%s)` does not contain the data access request `%s`",
         datasetId, datasetName, accessRequestId);

      return new AccessRequestNotFoundException(msg);
   }

}