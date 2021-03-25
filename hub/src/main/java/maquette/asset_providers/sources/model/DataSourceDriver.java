package maquette.asset_providers.sources.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataSourceDriver {

   POSTGRESQL("postgresql", "jdbc:postgresql");

   private final String value;

   private final String connectionPrefix;

   DataSourceDriver(String value, String connectionPrefix) {
      this.value = value;
      this.connectionPrefix = connectionPrefix;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

   public String getConnectionPrefix() {
      return connectionPrefix;
   }
   
}
