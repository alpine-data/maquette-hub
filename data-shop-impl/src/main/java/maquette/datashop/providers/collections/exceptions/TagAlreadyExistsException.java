package maquette.datashop.providers.collections.exceptions;

public final class TagAlreadyExistsException extends RuntimeException {

   private TagAlreadyExistsException(String message) {
      super(message);
   }

   public static TagAlreadyExistsException withName(String tag) {
      var msg = String.format("Tag `%s` already exists.", tag);
      return new TagAlreadyExistsException(msg);
   }

}
