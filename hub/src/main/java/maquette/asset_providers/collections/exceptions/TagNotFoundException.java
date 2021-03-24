package maquette.asset_providers.collections.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class TagNotFoundException extends RuntimeException implements DomainException {

   private TagNotFoundException(String message) {
      super(message);
   }

   public static TagNotFoundException withName(String tag) {
      var msg = String.format("Tag `%s` does not exist.", tag);
      return new TagNotFoundException(msg);
   }

}
