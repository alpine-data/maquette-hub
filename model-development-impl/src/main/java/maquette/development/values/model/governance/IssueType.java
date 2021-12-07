package maquette.development.values.model.governance;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IssueType {

   WARNING("warning"), CRITICAL("critical");

   private final String value;

   IssueType(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
