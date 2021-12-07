package maquette.development.entities.mlflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLModel {

   @JsonProperty("artifact_path")
   String artifactPath;

   Map<String, Map<String, String>> flavors;

   @JsonProperty("run_id")
   String runId;

}
