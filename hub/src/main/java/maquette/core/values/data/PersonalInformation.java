package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PersonalInformation {

   NONE("none"), PERSONAL_INFORMATION("pi"), SENSITIVE_PERSONAL_INFORMATION("spi");

   private final String value;

   PersonalInformation(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

   public static PersonalInformation forValue(String value) {
      if (value.equals(NONE.getValue())) {
         return NONE;
      } else if (value.equals(PERSONAL_INFORMATION.getValue())) {
         return PERSONAL_INFORMATION;
      } else if (value.equals(SENSITIVE_PERSONAL_INFORMATION.getValue())) {
         return SENSITIVE_PERSONAL_INFORMATION;
      } else {
         throw new IllegalArgumentException();
      }
   }

}
