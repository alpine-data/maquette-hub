package maquette.core.entities.data.model.access;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAccessRequestAction {

   RESPOND("respond", false, true),
   REQUEST("request", true, false),
   WITHDRAW("withdraw", true, true);

   @JsonValue
   private final String value;

   private final boolean canRequest;

   private final boolean canGrant;

   DataAccessRequestAction(String value, boolean canRequest, boolean canGrant) {
      this.value = value;
      this.canGrant = canGrant;
      this.canRequest = canRequest;
   }

   public String getValue() {
      return value;
   }

   public boolean isCanGrant() {
      return canGrant;
   }

   public boolean isCanRequest() {
      return canRequest;
   }

}
