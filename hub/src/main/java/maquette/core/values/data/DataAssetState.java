package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAssetState {

   REVIEW_REQUIRED("review-required"), APPROVED("approved"), DEPRECATED("deprecated");

   private final String value;

   DataAssetState(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
