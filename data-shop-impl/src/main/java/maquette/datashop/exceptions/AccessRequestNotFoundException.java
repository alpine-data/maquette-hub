package maquette.datashop.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public final class AccessRequestNotFoundException extends ApplicationException {

   private AccessRequestNotFoundException(String message) {
      super(message);
   }

   public static AccessRequestNotFoundException apply(UID accessRequestId) {
      var msg = String.format("Data access request `%s` not found.", accessRequestId);
      return new AccessRequestNotFoundException(msg);
   }

}
