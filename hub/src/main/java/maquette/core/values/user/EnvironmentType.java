package maquette.core.values.user;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnvironmentType {

   SANDBOX("sandbox"), EXTERNAL("external"), WEB("web");

   private final String value;

   EnvironmentType(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
