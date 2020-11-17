package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataVisibility {

   PUBLIC("public"), PRIVATE("private");

   private final String value;

   DataVisibility(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

   public static DataVisibility forValue(String value) {
      if (value.equals(PUBLIC.getValue())) {
         return PUBLIC;
      } else if (value.equals(PRIVATE.getValue())) {
         return PRIVATE;
      } else {
         throw new IllegalArgumentException();
      }
   }

}
