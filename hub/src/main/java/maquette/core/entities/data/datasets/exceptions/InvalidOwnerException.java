package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.exceptions.DomainException;

public class InvalidOwnerException extends RuntimeException implements DomainException {

   private InvalidOwnerException(String message) {
      super(message);
   }

   public static InvalidOwnerException apply() {
      var msg = "Only specific users are allowed to be owners of a data asset.";
      return new InvalidOwnerException(msg);
   }

}
