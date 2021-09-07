package maquette.core.modules.users.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class UserNotFoundException extends ApplicationException {

   private UserNotFoundException(String message) {
      super(message);
   }

   public static UserNotFoundException fromUserId(String userId) {
      String message = String.format("No user found with id `%s`", userId);
      return new UserNotFoundException(message);
   }

}
