package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataClassification {

   PUBLIC("public"), INTERNAL("internal"), CONFIDENTIAL("confidential"), RESTRICTED("restricted");

   private final String value;

   DataClassification(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

   public static DataClassification forValue(String value) {
      if (value.equals(PUBLIC.getValue())) {
         return PUBLIC;
      } else if (value.equals(INTERNAL.getValue())) {
         return INTERNAL;
      } else if (value.equals(CONFIDENTIAL.getValue())) {
         return CONFIDENTIAL;
      } else if (value.equals(RESTRICTED.getValue())) {
         return RESTRICTED;
      } else {
         throw new IllegalArgumentException();
      }
   }

}
