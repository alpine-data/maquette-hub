package maquette.core.entities.projects.exceptions;

import maquette.core.values.exceptions.DomainException;

public class ModelNotFoundException extends RuntimeException implements DomainException {

   private ModelNotFoundException(String message) {
      super(message);
   }

   public static ModelNotFoundException apply(String model) {
      var msg = String.format("Model `%s` does not exist.", model);
      return new ModelNotFoundException(msg);
   }

}
