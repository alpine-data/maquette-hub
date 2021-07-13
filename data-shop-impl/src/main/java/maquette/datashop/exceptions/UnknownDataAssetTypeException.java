package maquette.datashop.exceptions;

import maquette.core.common.exceptions.ApplicationException;

public final class UnknownDataAssetTypeException extends ApplicationException {

   private UnknownDataAssetTypeException(String message) {
      super(message);
   }

   public static UnknownDataAssetTypeException apply(String name) {
      String msg = String.format("There is no `%s` data asset type.", name);
      return new UnknownDataAssetTypeException(msg);
   }

}
