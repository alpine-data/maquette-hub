package maquette.core.entities.datasets.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class RevisionNotFoundException extends RuntimeException implements DomainException {

   private RevisionNotFoundException(String message) {
      super(message);
   }

   public static RevisionNotFoundException apply(String revisionId) {
      var msg = String.format("Dataset does not contain the revision id `%s`", revisionId);
      return new RevisionNotFoundException(msg);
   }

}
