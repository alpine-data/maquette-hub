package maquette.development.entities.mlflow.client;

import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.development.entities.mlflow.MlflowConfiguration;
import maquette.development.entities.mlflow.apimodel.MLModel;
import maquette.development.entities.mlflow.apimodel.ModelVersion;
import maquette.development.entities.mlflow.explainer.ExplainerArtifact;
import maquette.development.entities.mlflow.explainer.ShapashExplainer;
import org.apache.commons.compress.utils.Lists;
import org.mlflow.api.proto.Service;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Model version information as extracted from MLflow.
 * <p>
 * Some information is lazily loaded when requested.
 */
@AllArgsConstructor(staticName = "apply")
public class VersionFromRegistry {

    private final MlflowClient client;

    private final org.mlflow.tracking.MlflowClient mlflowClient;

    private final MlflowConfiguration configuration;

    private final ModelVersion mlflowVersion;

    @Nullable
    private Service.Run run;

    @Nullable
    private MLModel model;

    @Nullable
    private List<ExplainerArtifact> explainers;

    /**
     * Creates a new instance.
     *
     * @param client        The client to request information from MLflow via REST API.
     * @param mlflowClient  The MLflow client from MLflow Java SDK (used in favour of own REST APIs if possible).
     * @param mlflowVersion The version information extracted from Mlflow (from list versions API endpoint).
     * @return The new instance.
     */
    public static VersionFromRegistry apply(
        MlflowClient client,
        org.mlflow.tracking.MlflowClient mlflowClient,
        MlflowConfiguration configuration,
        ModelVersion mlflowVersion
    ) {
        return apply(client, mlflowClient, configuration, mlflowVersion, null, null, null);
    }

    /**
     * The name of the version.
     */
    public String getVersion() {
        return mlflowVersion.getVersion();
    }

    /**
     * The description of the model as retrieved from MLflow.
     */
    public String getDescription() {
        return Optional.ofNullable(mlflowVersion.getDescription()).orElse("");
    }

    /**
     * The moment the model version has been registered/ logged.
     */
    public Instant getCreated() {
        return Instant.ofEpochMilli(mlflowVersion.getCreationTimestamp());
    }

    public Instant getUpdated() {
        return Instant.ofEpochMilli(mlflowVersion.getLastUpdatedTimestamp());
    }

    /**
     * The current stage of the MLflow model.
     */
    public String getStage() {
        return Optional.ofNullable(mlflowVersion.getCurrentStage()).orElse("None");
    }

    /**
     * The name of the user who logged the model.
     */
    public String getUser() {
        return getRun().getInfo().getUserId();
    }

    /**
     * Flavours of the model as detected by MLflow.
     */
    public Set<String> getFlavors() {
        return getModel().getFlavors().keySet();
    }

    /**
     * A list of logged explainer artifacts for the model version.
     */
    public synchronized List<ExplainerArtifact> getExplainers() {
        if (Objects.isNull(this.explainers)) {
            explainers = Lists.newArrayList();

            var explainerPath = String.format(
                "%s/get-artifact?path=xpl.pkl&run_uuid=%s",
                configuration.getMlflowBasePath(),
                mlflowVersion.getRunId());

            client.downloadFile(explainerPath).ifPresent(
                file -> explainers.add(ShapashExplainer.apply("xpl.pkl"))
            );

            explainers = List.copyOf(explainers);
        }

        return explainers;
    }

    /**
     * The Git commit id of the training code of the model.
     */
    public Optional<String> getGitCommit() {
        return getRun()
            .getData()
            .getTagsList()
            .stream()
            .filter(
                tag -> tag.getKey().equals("mlflow.source.git.commit")
            )
            .map(Service.RunTag::getValue)
            .findFirst();
    }

    /**
     * The git repository URL of the model's training code.
     */
    public Optional<String> getGitUrl() {
        return getRun()
            .getData()
            .getTagsList()
            .stream()
            .filter(tag -> tag
                .getKey()
                .equals("mlflow.source.git.repoURL"))
            .map(Service.RunTag::getValue)
            .findFirst();
    }

    private synchronized Service.Run getRun() {
        if (Objects.isNull(this.run)) {
            this.run = mlflowClient.getRun(this.mlflowVersion.getRunId());
        }

        return this.run;
    }

    private synchronized MLModel getModel() {
        if (Objects.isNull(this.model)) {
            var source = this.mlflowVersion.getSource();

            var modelPath = source.substring(
                source.indexOf("artifacts") + "artifacts/".length()
            );

            var downloadPath = String.format(
                "%s/get-artifact?path=%s%%2FMLmodel&run_uuid=%s",
                configuration.getMlflowBasePath(),
                modelPath.replace("/", "%2F"),
                this.mlflowVersion.getRunId());

            this.model = Operators.suppressExceptions(() -> DefaultObjectMapperFactory
                .apply()
                .createYamlMapper()
                .readValue(client.download(downloadPath), MLModel.class));
        }

        return this.model;
    }

}
