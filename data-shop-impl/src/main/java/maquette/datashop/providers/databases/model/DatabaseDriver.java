package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DatabaseDriver {

   POSTGRESQL("postgresql", "jdbc:postgresql"),
   MSSQL("mssql", "jdbc:sqlserver");

   private final String value;

   private final String connectionPrefix;

   DatabaseDriver(String value, String connectionPrefix) {
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
