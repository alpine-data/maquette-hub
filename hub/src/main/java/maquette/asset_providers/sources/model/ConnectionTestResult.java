package maquette.asset_providers.sources.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ConnectionTestResult {

   @JsonProperty("result")
   String getResult();

}
