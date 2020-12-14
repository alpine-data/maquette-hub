package maquette.core.entities.data.datasources.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ConnectionTestResult {

   @JsonProperty("result")
   String getResult();

}
