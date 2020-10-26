package maquette.core.values.exceptions;

public class MaquetteUserException extends RuntimeException {

   public MaquetteUserException(String message, Throwable cause) {
      super(message, cause);
   }

   public MaquetteUserException(String message) {
      this(message, null);
   }

}
