package maquette.core.entities.data.streams.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class StreamAlreadyExistsException extends RuntimeException implements DomainException {

   private StreamAlreadyExistsException(String message) {
      super(message);
   }

   public static StreamAlreadyExistsException withName(String stream) {
      var msg = String.format("Stream `%s` already exists.", stream);
      return new StreamAlreadyExistsException(msg);
   }

}
