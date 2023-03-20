package maquette.development.entities.mlflow.client;

import lombok.AllArgsConstructor;
import maquette.development.entities.mlflow.MlflowConfiguration;
import maquette.development.entities.mlflow.apimodel.ModelVersionsResponse;
import maquette.development.entities.mlflow.apimodel.RegisteredModel;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents model information as extracted from MLflow.
 * <p>
 * Separate client class to extract information for a specific model when requested.
 */
@AllArgsConstructor(staticName = "apply")
public class ModelFromRegistry {

    /**
     * The MLflow client used to communicate with MLflow.
     */
    private final MlflowClient client;

    private final org.mlflow.tracking.MlflowClient mlflowClient;

    private final MlflowConfiguration configuration;

    /**
     * Base information received from MLflow.
     */
    private final RegisteredModel registeredModel;

    /**
     * The list of versions available from MLflow.
     * This list is lazily initialized when getter is called.
     */
    @Nullable
    private List<VersionFromRegistry> versions;

    /**
     * Creates a new instance.
     *
     * @param client The client to request information from MLflow via REST API.
     * @param mlflowClient The MLflow client from MLflow Java SDK (used in favour of own REST APIs if possible).
     * @param registeredModel The fetched model information from MLflow.
     * @return A new instance.
     */
    public static ModelFromRegistry apply(
        MlflowClient client,
        org.mlflow.tracking.MlflowClient mlflowClient,
        MlflowConfiguration configuration,
        RegisteredModel registeredModel) {

        return apply(client, mlflowClient, configuration, registeredModel, null);
    }

    /**
     * The name of the model as tracked in MLflow.
     */
    public String getName() {
        return registeredModel.getName();
    }

    /**
     * The moment when the model was registered.
     */
    public Instant getCreated() {
        return Instant.ofEpochMilli(registeredModel.getCreationTimestamp());
    }

    /**
     * The moment the model was last updated in MLflow.
     */
    public Instant getUpdated() {
        return Instant.ofEpochMilli(registeredModel.getLastUpdatedTimestamp());
    }

    /**
     * A list of version of the model.
     */
    public synchronized List<VersionFromRegistry> getVersions() {
        if (Objects.isNull(this.versions)) {
            this.versions = client
                .query(
                    String.format(
                        "/api/2.0/preview/mlflow/model-versions/search?filter=name%%3D%%27%s%%27",
                        this.getName()
                    ),
                    ModelVersionsResponse.class
                )
                .getModelVersions()
                .stream()
                .map(modelVersion -> VersionFromRegistry.apply(
                    client, mlflowClient, configuration, modelVersion
                ))
                .collect(Collectors.toList());
        }

        return versions;
    }

}
