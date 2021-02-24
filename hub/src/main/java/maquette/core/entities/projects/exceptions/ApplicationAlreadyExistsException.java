package maquette.core.entities.projects.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class ApplicationAlreadyExistsException extends RuntimeException implements DomainException {

   private ApplicationAlreadyExistsException(String message) {
      super(message);
   }

   public static ApplicationAlreadyExistsException apply(String application) {
      var msg = String.format("An application with the name `%s` already exists.", application);
      return new ApplicationAlreadyExistsException(msg);
   }

}
