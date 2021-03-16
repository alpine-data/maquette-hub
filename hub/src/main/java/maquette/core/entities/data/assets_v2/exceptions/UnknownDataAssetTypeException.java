package maquette.core.entities.data.assets_v2.exceptions;

import maquette.core.values.exceptions.DomainException;

public final class UnknownDataAssetTypeException extends RuntimeException implements DomainException {

   private UnknownDataAssetTypeException(String message) {
      super(message);
   }

   public static UnknownDataAssetTypeException apply(String name) {
      String msg = String.format("There is no `%s` data asset type.", name);
      return new UnknownDataAssetTypeException(msg);
   }

}
