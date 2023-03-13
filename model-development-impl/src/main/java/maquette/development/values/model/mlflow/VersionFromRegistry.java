package maquette.development.values.model.mlflow;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.development.entities.mlflow.explainer.ExplainerArtifact;
import maquette.development.values.model.ModelExplainer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Model version information as extracted from MLflow.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class VersionFromRegistry {

    /**
     * The name of the version.
     */
    String version;

    /**
     * The description of the model as retrieved from MLflow.
     */
    String description;

    /**
     * The moment the model version has been registered/ logged.
     */
    Instant created;

    /**
     * The current stage of the MLflow model.
     */
    String stage;

    /**
     * The name of the user who logged the model.
     */
    String user;

    /**
     * The Git commit id of the training code of the model.
     */
    String gitCommit;

    /**
     * The git repository URL of the model's training code.
     */
    String gitUrl;

    /**
     * Flavours of the model as detected by MLflow.
     */
    Set<String> flavors;

    /**
     * A list of logged explainer artifacts for the model version.
     */
    List<ExplainerArtifact> explainers;

    /**
     * See {@link VersionFromRegistry#gitCommit}
     * @return The git commit id.
     */
    public Optional<String> getGitCommit() {
        return Optional.ofNullable(gitCommit);
    }

    /**
     * See {@link VersionFromRegistry#gitUrl}
     * @return The Git repository URL if present.
     */
    public Optional<String> getGitUrl() {
        return Optional.ofNullable(gitUrl);
    }

}
