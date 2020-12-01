package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class AccessRequestNotFoundException extends RuntimeException implements DomainException {

   private AccessRequestNotFoundException(String message) {
      super(message);
   }

   public static AccessRequestNotFoundException apply(UID accessRequestId) {
      var msg = String.format("Data access request `%s` not found.", accessRequestId);
      return new AccessRequestNotFoundException(msg);
   }

}
