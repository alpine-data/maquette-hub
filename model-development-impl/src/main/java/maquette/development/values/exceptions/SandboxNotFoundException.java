package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public final class SandboxNotFoundException extends ApplicationException {

   private SandboxNotFoundException(String message) {
      super(message);
   }

   public static SandboxNotFoundException applyFromName(String name) {
      String msg = String.format("Sandbox with name `%s` was not found.", name);
      return new SandboxNotFoundException(msg);
   }

   public static SandboxNotFoundException applyFromId(UID id) {
      String msg = String.format("Sandbox with id `%s` was not found.", id);
      return new SandboxNotFoundException(msg);
   }

   @Override
   public int getHttpStatus() {
      return 404;
   }
}
