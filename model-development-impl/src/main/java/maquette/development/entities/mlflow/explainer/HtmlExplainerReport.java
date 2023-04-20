package maquette.development.entities.mlflow.explainer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

/**
 * See {@link ExplainerArtifact} for description adn purpose.
 */
@Value
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlExplainerReport implements ExplainerArtifact {

    private static final String ARTIFACT_PATH = "artifactPath";

    /**
     * The path of the shapash explainer file within the MLflow experiment/ logged artifacts.
     */
    @JsonProperty(ARTIFACT_PATH)
    String artifactPath;

    /**
     * Creates a new instance (from JSON).
     *
     * @param artifactPath See {@link HtmlExplainerReport#artifactPath}.
     * @return A new instance.
     */
    @JsonCreator
    public static HtmlExplainerReport apply(
        @JsonProperty(ARTIFACT_PATH) String artifactPath) {

        return new HtmlExplainerReport(artifactPath);
    }

}
