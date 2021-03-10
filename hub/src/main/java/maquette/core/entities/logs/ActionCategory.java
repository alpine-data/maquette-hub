package maquette.core.entities.logs;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionCategory {

   ADMINISTRATION("administration"), READ("read"), WRITE("write"), VIEW("view");

   private final String value;

   ActionCategory(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
