package maquette.core.entities.users.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class UserNotFoundException extends RuntimeException implements DomainException {

   private UserNotFoundException(String message) {
      super(message);
   }

   public static UserNotFoundException fromUserId(String userId) {
      String message = String.format("No user found with id `%s`", userId);
      return new UserNotFoundException(message);
   }

}
