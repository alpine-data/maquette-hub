package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAssetMemberRole {

   PRODUCER("producer"), CONSUMER("consumer"), MEMBER("member"), OWNER("owner");

   private final String value;

   DataAssetMemberRole(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
