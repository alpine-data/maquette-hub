package maquette.operations.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Value class which represents stored information of a deployed model service and its instances.
 * <p>
 * For deployed model services, the combination of {@link DeployedModelService#name} and
 * {@link DeployedModelService#gitRepositoryUrl} must be unique.
 * A model service (and its instances) may use multiple models and multiple versions of a model.
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeployedModelService {

    private static final String NAME = "name";
    private static final String GIT_REPOSITORY_URL = "gitRepositoryUrl";
    private static final String BACKSTAGE_CATALOG_URL = "backstageCatalogUrl";
    private static final String INSTANCES = "instances";

    /**
     * The unique name of the service.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * The Git Repository URL for the source code of the Service's source code.
     */
    @JsonProperty(GIT_REPOSITORY_URL)
    String gitRepositoryUrl;

    /**
     * The catalog URL for the service in Backstage.
     */
    @JsonProperty(BACKSTAGE_CATALOG_URL)
    String backstageCatalogUrl;

    /**
     * List of registered instances.
     */
    @With
    @JsonProperty(INSTANCES)
    List<DeployedModelServiceInstance> instances;

    /**
     * Creates a new instance (from JSON).
     *
     * @param name                See {@link DeployedModelService#name}.
     * @param gitRepositoryUrl    See {@link DeployedModelService#gitRepositoryUrl}.
     * @param backstageCatalogUrl See {@link DeployedModelService#backstageCatalogUrl}.
     * @param instances           See {@link DeployedModelService#instances}.
     * @return A new instance.
     */
    @JsonCreator
    public static DeployedModelService apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(GIT_REPOSITORY_URL) String gitRepositoryUrl,
        @JsonProperty(BACKSTAGE_CATALOG_URL) String backstageCatalogUrl,
        @JsonProperty(INSTANCES) List<DeployedModelServiceInstance> instances
    ) {
        if (Objects.isNull(instances)) {
            instances = List.of();
        }

        return new DeployedModelService(name, gitRepositoryUrl, backstageCatalogUrl, instances);
    }

    /**
     * Creates a new instance without any service instances.
     *
     * @param name                See {@link DeployedModelService#name}.
     * @param gitRepositoryUrl    See {@link DeployedModelService#gitRepositoryUrl}.
     * @param backstageCatalogUrl See {@link DeployedModelService#backstageCatalogUrl}.
     * @return A new instance.
     */
    public static DeployedModelService apply(String name, String gitRepositoryUrl, String backstageCatalogUrl) {
        return apply(name, gitRepositoryUrl, backstageCatalogUrl, List.of());
    }

    /**
     * Compares the identity (name and git repository URL) of two instances.
     *
     * @param other The instance to compare.
     * @return True if identity is equal.
     */
    public boolean sameIdentity(DeployedModelService other) {
        return this.getName().equals(other.getName()) &&
            this.getGitRepositoryUrl().equals(other.getGitRepositoryUrl());
    }

    /**
     * Compares the identity (name and git repository URL) to check whether identity is the same.
     *
     * @param name             The name of the other candidate.
     * @param gitRepositoryUrl The repository Url of the other candidate.
     * @return True if identity is equal.
     */
    public boolean sameIdentity(String name, String gitRepositoryUrl) {
        return this.getName().equals(name) && this.getGitRepositoryUrl().equals(gitRepositoryUrl);
    }

    /**
     * Creates a new instance including the new instances. If an entry for the instance has been existing before,
     * it will be replaced.
     *
     * @param instance The new instance to be updated or inserted.
     * @return The new instance.
     */
    public DeployedModelService withInstance(DeployedModelServiceInstance instance) {
        var instances = Stream
            .concat(
                this
                    .instances
                    .stream()
                    .filter(existingInstance -> !existingInstance.getUrl().equals(instance.getUrl())),
                Stream.of(instance)
            )
            .collect(Collectors.toList());

        return this.withInstances(instances);
    }

}
