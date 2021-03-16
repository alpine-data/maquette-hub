package maquette.core.entities.data.assets_v2.exceptions;

import maquette.core.values.UID;
import maquette.core.values.exceptions.DomainException;

public final class DataAssetNotFoundException extends RuntimeException implements DomainException {

   private DataAssetNotFoundException(String message) {
      super(message);
   }

   public static DataAssetNotFoundException applyFromName(String name) {
      String msg = String.format("Data Asset with name `%s` was not found.", name);
      return new DataAssetNotFoundException(msg);
   }

   public static DataAssetNotFoundException applyFromId(UID asset) {
      String msg = String.format("Data Asset with id `%s` was not found.", asset);
      return new DataAssetNotFoundException(msg);
   }

}
