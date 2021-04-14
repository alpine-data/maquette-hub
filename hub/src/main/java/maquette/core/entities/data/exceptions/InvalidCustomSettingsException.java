package maquette.core.entities.data.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class InvalidCustomSettingsException extends RuntimeException implements DomainException {

   private InvalidCustomSettingsException(String message) {
      super(message);
   }

   public static InvalidCustomSettingsException apply(String type, Class<?> actual, Class<?> expected) {
      String msg = String.format(
         "Invalid custom properties for data asset type `s`. Expected `%s`; Actual: `%s`.", type, expected, actual);

      return new InvalidCustomSettingsException(msg);
   }

}
