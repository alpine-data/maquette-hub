package maquette.asset_providers.collections.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class TagAlreadyExistsException extends RuntimeException implements DomainException {

   private TagAlreadyExistsException(String message) {
      super(message);
   }

   public static TagAlreadyExistsException withName(String tag) {
      var msg = String.format("Tag `%s` already exists.", tag);
      return new TagAlreadyExistsException(msg);
   }

}
