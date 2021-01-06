package maquette.core.values.data.binary;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAccessType {

   CONSUME("consume"), PRODUCE("produce");

   private final String value;

   DataAccessType(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
