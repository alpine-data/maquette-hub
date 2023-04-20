package maquette.development.entities.mlflow.apimodel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfo {

    String path;

    @JsonProperty("is_dir")
    Boolean isDir;

    @JsonProperty("file_size")
    int fileSize;

}
