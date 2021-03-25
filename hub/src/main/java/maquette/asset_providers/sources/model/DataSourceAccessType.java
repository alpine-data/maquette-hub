package maquette.asset_providers.sources.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataSourceAccessType {

   DIRECT("direct"), CACHED("cached");

   private final String value;

   DataSourceAccessType(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
