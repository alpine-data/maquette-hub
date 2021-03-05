package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataZone {

   RAW("raw"), PREPARED("prepared"), GOLD("gold");

   private final String value;

   DataZone(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
