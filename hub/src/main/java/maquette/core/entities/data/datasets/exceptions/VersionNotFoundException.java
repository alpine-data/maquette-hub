package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class VersionNotFoundException extends RuntimeException implements DomainException {

   private VersionNotFoundException(String message) {
      super(message);
   }

   public static VersionNotFoundException apply(String version) {
      var msg = String.format("Dataset does not contain the version `%s`", version);
      return new VersionNotFoundException(msg);
   }

}