package maquette.asset_providers.streams;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DurationUnit {

   SECONDS("seconds"), MINUTES("minutes"), HOURS("hours"), DAYS("days");

   private final String value;

   DurationUnit(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
