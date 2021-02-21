package maquette.core.entities.projects.model.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModelMemberRole {

   OWNER("owner"), SME("sme"), REVIEWER("reviewer"), DATA_SCIENTIST("ds");

   private final String value;

   ModelMemberRole(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
