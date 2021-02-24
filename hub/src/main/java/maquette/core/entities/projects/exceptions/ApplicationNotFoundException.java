package maquette.core.entities.projects.exceptions;

import maquette.core.values.exceptions.DomainException;

public class ApplicationNotFoundException extends RuntimeException implements DomainException {

   private ApplicationNotFoundException(String message) {
      super(message);
   }

   public static ApplicationNotFoundException apply(String application) {
      var msg = String.format("Application `%s` does not exist.", application);
      return new ApplicationNotFoundException(msg);
   }

}
