package maquette.core.entities.data.collections.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class CollectionAlreadyExistsException extends RuntimeException implements DomainException {

   private CollectionAlreadyExistsException(String message) {
      super(message);
   }

   public static CollectionAlreadyExistsException withName(String collection) {
      var msg = String.format("Collection `%s` already exists.", collection);
      return new CollectionAlreadyExistsException(msg);
   }

}
