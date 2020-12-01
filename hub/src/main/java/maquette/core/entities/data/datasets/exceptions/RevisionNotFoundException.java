package maquette.core.entities.data.datasets.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class RevisionNotFoundException extends RuntimeException implements DomainException {

   private RevisionNotFoundException(String message) {
      super(message);
   }

   public static RevisionNotFoundException apply(UID revisionId) {
      var msg = String.format("Dataset does not contain the revision id `%s`", revisionId);
      return new RevisionNotFoundException(msg);
   }

}
