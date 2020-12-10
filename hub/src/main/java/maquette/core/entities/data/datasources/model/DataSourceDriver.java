package maquette.core.entities.data.datasources.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataSourceDriver {

   POSTGRESQL("postgresql");

   private final String value;

   DataSourceDriver(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }
}
