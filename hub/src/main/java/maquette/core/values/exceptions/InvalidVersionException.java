package maquette.core.values.exceptions;

public final class InvalidVersionException extends IllegalArgumentException implements DomainException {

   private InvalidVersionException(String message) {
      super(message);
   }

   public static InvalidVersionException apply(String version) {
      String message = String.format(
         "The provided version '%s' is not a valid.",
         version);

      return new InvalidVersionException(message);
   }

}
