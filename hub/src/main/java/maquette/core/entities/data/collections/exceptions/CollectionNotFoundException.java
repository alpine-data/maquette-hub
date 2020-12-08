package maquette.core.entities.data.collections.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class CollectionNotFoundException extends RuntimeException implements DomainException {

   private CollectionNotFoundException(String message) {
      super(message);
   }

   public static CollectionNotFoundException withName(String name) {
      var msg = String.format("Collection `%s` does not exist.", name);
      return new CollectionNotFoundException(msg);
   }

   public static CollectionNotFoundException withId(UID id) {
      var msg = String.format("Collection with id `%s` not found.", id);
      return new CollectionNotFoundException(msg);
   }

}
