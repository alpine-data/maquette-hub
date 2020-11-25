package maquette.core.values.exceptions;

public class NotAuthorizedException extends RuntimeException implements DomainException {

   private NotAuthorizedException(String message) {
      super(message);
   }

   public static NotAuthorizedException apply(String message) {
      return new NotAuthorizedException(message);
   }

}
