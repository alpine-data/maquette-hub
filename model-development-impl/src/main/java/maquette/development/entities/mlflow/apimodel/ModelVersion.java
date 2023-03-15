package maquette.development.entities.mlflow.apimodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ModelVersion {

    private String name;

    private String version;

    @JsonProperty("creation_timestamp")
    private long creationTimestamp;

    @JsonProperty("last_updated_timestamp")
    private long lastUpdatedTimestamp;

    @JsonProperty("current_stage")
    private String currentStage;

    private String description;

    @JsonProperty("run_id")
    private String runId;

    private String source;

}
