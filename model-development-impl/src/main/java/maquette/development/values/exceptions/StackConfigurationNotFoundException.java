package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public class StackConfigurationNotFoundException extends ApplicationException {

   private StackConfigurationNotFoundException(String message) {
      super(message);
   }

   public static StackConfigurationNotFoundException applyFromId(UID id) {
      String msg = String.format("StackConfiguration with id `%s` was not found.", id);
      return new StackConfigurationNotFoundException(msg);
   }

}
