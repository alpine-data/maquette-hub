package maquette.datashop.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class InvalidCustomSettingsException extends ApplicationException {

   private InvalidCustomSettingsException(String message) {
      super(message);
   }

   public static InvalidCustomSettingsException apply(String type, Class<?> actual, Class<?> expected) {
      String msg = String.format(
         "Invalid custom properties for data asset type `s`. Expected `%s`; Actual: `%s`.", type, expected, actual);

      return new InvalidCustomSettingsException(msg);
   }

}
