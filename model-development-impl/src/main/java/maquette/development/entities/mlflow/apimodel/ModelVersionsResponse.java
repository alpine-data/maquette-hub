package maquette.development.entities.mlflow.apimodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ModelVersionsResponse {

    @JsonProperty("model_versions")
    private List<ModelVersion> modelVersions;

}
