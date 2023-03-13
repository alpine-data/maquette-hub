package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.development.values.exceptions.ModelVersionNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents atomic values of a Model.
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelProperties {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String VERSIONS = "versions";
    private static final String CREATED = "created";
    private static final String UPDATED = "updated";

    /**
     * The name of the model. Should be the same name as within MLflow.
     */
    @JsonProperty(NAME)
    String name;

    /**
     * The description of the model.
     */
    @JsonProperty(DESCRIPTION)
    String description;

    /**
     * Available versions of the model. There is always as at least one version.
     */
    @JsonProperty(VERSIONS)
    List<ModelVersion> versions;

    /**
     * The moment in which the first version of the model was registered in MLflow.
     */
    @JsonProperty(CREATED)
    ActionMetadata created;

    /**
     * The moment when the model was updated.
     */
    @JsonProperty(UPDATED)
    ActionMetadata updated;

    /**
     * Creates a new instance. Also used to create instances from JSON representation.
     *
     * @param name See {@link ModelProperties#name}.
     * @param description See {@link ModelProperties#description}.
     * @param versions See {@link ModelProperties#versions}.
     * @param created See {@link ModelProperties#created}.
     * @param updated See {@link ModelProperties#updated}.
     * @return A new instance.
     */
    @JsonCreator
    public static ModelProperties apply(
        @JsonProperty("NAME") String name,
        @JsonProperty("DESCRIPTION") String description,
        @JsonProperty("VERSIONS") List<ModelVersion> versions,
        @JsonProperty("CREATED") ActionMetadata created,
        @JsonProperty("UPDATED") ActionMetadata updated
    ) {
        return new ModelProperties(name, description, versions, created, updated);
    }

    /**
     * Creates a fake instance of {@link ModelProperties}. Can be used for testing purposes.
     *
     * @return A fake instance with dummy values.
     */
    public static ModelProperties fake() {
        return ModelProperties.apply(
            "some-model",
            "Lorem ipsum", List.of(ModelVersion.fake()),
            ActionMetadata.apply("Egon Olsen"), ActionMetadata.apply("Donald Duck"));
    }

    /**
     * Returns version by its name.
     *
     * @param version The name of the version as listed in MLflow.
     * @return The matching version.
     * @throws ModelVersionNotFoundException if version cannot be found.
     */
    public ModelVersion getVersion(String version) {
        return findVersion(version).orElseThrow(() -> ModelVersionNotFoundException.apply(name, version));
    }

    /**
     * Find a version by its name.
     *
     * @param version The name of the version as listed in MLflow.
     * @return The matching version or none.
     */
    public Optional<ModelVersion> findVersion(String version) {
        return versions
            .stream()
            .filter(v -> v
                .getVersion()
                .equals(version))
            .findAny();
    }

    /**
     * Returns a new instance of {@link ModelProperties} with inserted or updated version.
     *
     * @param version The version to update/ insert.
     * @return A new instance.
     */
    public ModelProperties withVersion(ModelVersion version) {
        var filtered = this
            .versions
            .stream()
            .filter(v -> !v
                .getVersion()
                .equals(version.getVersion()));

        var versions = Stream
            .concat(filtered, Stream.of(version))
            .collect(Collectors.toList());

        return withVersions(versions);
    }

}
