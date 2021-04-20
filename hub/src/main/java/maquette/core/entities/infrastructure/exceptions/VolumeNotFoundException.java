package maquette.core.entities.infrastructure.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class VolumeNotFoundException extends RuntimeException implements DomainException {

   private VolumeNotFoundException(String message) {
      super(message);
   }

   public static VolumeNotFoundException apply(UID volume) {
      var msg = String.format("No volume with id `%s` found.", volume.getValue());
      return new VolumeNotFoundException(msg);
   }

}
