package maquette.core.entities.data.datasources.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EDataSourceDriver {

   POSTGRE_SQL("postgre-sql");

   private final String value;

   EDataSourceDriver(String value) {
      this.value = value;
   }

   @JsonValue
   public String getValue() {
      return value;
   }

}
