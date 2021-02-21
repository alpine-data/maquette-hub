package maquette.core.entities.projects.exceptions;

import maquette.core.values.exceptions.DomainException;

public class ModelVersionNotFoundException extends RuntimeException implements DomainException {

   private ModelVersionNotFoundException(String message) {
      super(message);
   }

   public static ModelVersionNotFoundException apply(String model, String version) {
      var msg = String.format("Model version `%s` does not exist in model `%s`.", version, model);
      return new ModelVersionNotFoundException(msg);
   }

}
