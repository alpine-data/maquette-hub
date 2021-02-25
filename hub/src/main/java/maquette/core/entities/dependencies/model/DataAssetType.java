package maquette.core.entities.dependencies.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAssetType {

   COLLECTION("collection"), DATASET("dataset"), SOURCE("source"), STREAM("stream");

   private final String value;

   DataAssetType(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }
}
