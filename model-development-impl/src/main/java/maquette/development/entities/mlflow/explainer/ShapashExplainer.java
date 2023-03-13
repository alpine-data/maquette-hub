package maquette.development.entities.mlflow.explainer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * See {@link ExplainerArtifact} for description adn purpose.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShapashExplainer implements ExplainerArtifact {

    private static final String ARTIFACT_PATH = "artifactPath";

    /**
     * The path of the shapash explainer file within the MLflow experiment/ logged artifacts.
     */
    @JsonProperty(ARTIFACT_PATH)
    String artifactPath;

    /**
     * Creates a new instance (from JSON).
     *
     * @param artifactPath See {@link ShapashExplainer#artifactPath}.
     * @return A new instance.
     */
    @JsonCreator
    public static ShapashExplainer apply(
        @JsonProperty(ARTIFACT_PATH) String artifactPath) {

        return new ShapashExplainer(artifactPath);
    }

}
