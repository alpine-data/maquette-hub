package maquette.core.entities.data.assets_v2.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class DataAssetAlreadyExistsException extends RuntimeException implements DomainException {

   private DataAssetAlreadyExistsException(String message) {
      super(message);
   }

   public static DataAssetAlreadyExistsException withName(String name) {
      var msg = String.format("A data asset with name `%s` already exists.", name);
      return new DataAssetAlreadyExistsException(msg);
   }

}
