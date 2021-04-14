package maquette.core.entities.data.exceptions;

public final class InvalidCustomPropertiesException extends RuntimeException {

   private InvalidCustomPropertiesException(String message) {
      super(message);
   }

   public static InvalidCustomPropertiesException apply(String type, Class<?> actual, Class<?> expected) {
      String msg = String.format(
         "Invalid custom properties for data asset type `%s`. Expected `%s`; Actual: `%s`.", type, expected, actual);

      return new InvalidCustomPropertiesException(msg);
   }

}
