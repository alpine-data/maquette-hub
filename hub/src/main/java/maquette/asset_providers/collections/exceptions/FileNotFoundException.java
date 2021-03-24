package maquette.asset_providers.collections.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class FileNotFoundException extends RuntimeException implements DomainException {

   private FileNotFoundException(String message) {
      super(message);
   }

   public static FileNotFoundException withName(String name) {
      var msg = String.format("File `%s` does not exist.", name);
      return new FileNotFoundException(msg);
   }

}
