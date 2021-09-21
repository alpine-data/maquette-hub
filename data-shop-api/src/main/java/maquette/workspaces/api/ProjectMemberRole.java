package maquette.workspaces.api;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectMemberRole {

   ADMIN("admin"), MEMBER("member");

   private final String value;

   ProjectMemberRole(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
