package maquette.datashop.providers.databases.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ConnectionTestResult {

    @JsonProperty("result")
    String getResult();

}
