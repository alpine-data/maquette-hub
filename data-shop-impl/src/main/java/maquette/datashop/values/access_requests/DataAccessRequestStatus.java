package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAccessRequestStatus {
   REQUESTED("requested"),
   GRANTED("granted"),
   REJECTED("rejected"),
   EXPIRED("expired"),
   WITHDRAWN("withdrawn");

   @JsonValue
   private final String value;

   DataAccessRequestStatus(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }

}
