package maquette.core.entities.projects.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public class SandboxNotFoundException extends RuntimeException implements DomainException  {

   private SandboxNotFoundException(String message) {
      super(message);
   }

   public static SandboxNotFoundException apply(UID id) {
      var msg = String.format("Sandbox with id `%s` not found.", id);
      return new SandboxNotFoundException(msg);
   }

   public static SandboxNotFoundException apply(String name) {
      var msg = String.format("Sandbox with name `%s` not found.", name);
      return new SandboxNotFoundException(msg);
   }

}
