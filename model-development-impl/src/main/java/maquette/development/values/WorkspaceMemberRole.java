package maquette.development.values;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkspaceMemberRole {

   ADMIN("admin"), MEMBER("member");

   private final String value;

   WorkspaceMemberRole(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
