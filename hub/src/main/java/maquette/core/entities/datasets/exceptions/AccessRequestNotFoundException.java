package maquette.core.entities.datasets.exceptions;

import maquette.core.values.exceptions.MaquetteUserException;

public class AccessRequestNotFoundException extends MaquetteUserException {

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
