package maquette.operations.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Value class to store properties of a deployed model service instance and its current runtime state.
 * <p>
 * The runtime state is checked by Maquette on regular basis. And updated accordingly.
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeployedModelServiceInstance {

    private static final String URL = "url";
    private static final String MODELS = "models";
    private static final String ENVIRONMENT = "environment";
    private static final String STATUS = "status";
    private static final String LAST_CHECKED = "lastChecked";

    /**
     * The URL of the service (API). This is used as unique identifier for service instances.
     */
    @JsonProperty(URL)
    String url;

    /**
     * The set of deployed models in this service instance.
     */
    Set<DeployedModelVersion> models;

    /**
     * The name of the environment to which it is deployed.
     */
    @JsonProperty(ENVIRONMENT)
    String environment;

    /**
     * The status of the service, as checked by Maquette.
     */
    @JsonProperty(STATUS)
    DeployedModelServiceInstanceStatus status;

    /**
     * The moment in which Maquette checked the status of the service the last time.
     */
    @JsonProperty(LAST_CHECKED)
    Instant lastChecked;

    /**
     * Creates a new instance (from JSON).
     *
     * @param url         See {@link DeployedModelServiceInstance#url}.
     * @param environment See {@link DeployedModelServiceInstance#environment}.
     * @param status      See {@link DeployedModelServiceInstance#status}.
     * @param lastChecked See {@link DeployedModelServiceInstance#lastChecked}.
     * @param models      See {@link DeployedModelServiceInstance#models}.
     * @return The new instance.
     */
    @JsonCreator
    public static DeployedModelServiceInstance apply(
        @JsonProperty(URL) String url,
        @JsonProperty(ENVIRONMENT) String environment,
        @JsonProperty(STATUS) DeployedModelServiceInstanceStatus status,
        @JsonProperty(LAST_CHECKED) Instant lastChecked,
        @JsonProperty(MODELS) Set<DeployedModelVersion> models
    ) {
        if (Objects.isNull(models)) {
            models = Set.of();
        }

        return new DeployedModelServiceInstance(url, models, environment, status, lastChecked);
    }

    /**
     * Creates a new instance with an empty set of used models.
     *
     * @param url         See {@link DeployedModelServiceInstance#url}.
     * @param environment See {@link DeployedModelServiceInstance#environment}.
     * @param status      See {@link DeployedModelServiceInstance#status}.
     * @param lastChecked See {@link DeployedModelServiceInstance#lastChecked}.
     * @return The new instance.
     */
    public static DeployedModelServiceInstance apply(
        String url, String environment,
        DeployedModelServiceInstanceStatus status, Instant lastChecked
    ) {
        return new DeployedModelServiceInstance(url, Set.of(), environment, status, lastChecked);
    }

}
