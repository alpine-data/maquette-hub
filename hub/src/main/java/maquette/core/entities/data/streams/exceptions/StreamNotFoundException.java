package maquette.core.entities.data.streams.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class StreamNotFoundException extends RuntimeException implements DomainException {

   private StreamNotFoundException(String message) {
      super(message);
   }

   public static StreamNotFoundException withName(String name) {
      var msg = String.format("Stream `%s` does not exist.", name);
      return new StreamNotFoundException(msg);
   }

   public static StreamNotFoundException withId(UID id) {
      var msg = String.format("Stream with id `%s` not found.", id);
      return new StreamNotFoundException(msg);
   }

}
