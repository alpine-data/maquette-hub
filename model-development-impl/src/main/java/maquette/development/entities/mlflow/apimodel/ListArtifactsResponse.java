package maquette.development.entities.mlflow.apimodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListArtifactsResponse {

    @JsonProperty("root_uri")
    String rootUri;

    @JsonProperty("files")
    private List<FileInfo> files;

    @JsonProperty("next_page_token")
    private String nextPageToken;
}
