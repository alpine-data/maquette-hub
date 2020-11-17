package maquette.core.entities.sandboxes.exceptions;

import maquette.core.values.exceptions.DomainException;

public class SandboxNotFoundException extends RuntimeException implements DomainException  {

   private SandboxNotFoundException(String message) {
      super(message);
   }

   public static SandboxNotFoundException apply(String id) {
      var msg = String.format("Workspace with id `%s` not found.", id);
      return new SandboxNotFoundException(msg);
   }

}
