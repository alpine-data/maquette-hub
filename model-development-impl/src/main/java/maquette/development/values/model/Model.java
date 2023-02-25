package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.values.model.governance.CheckExemption;
import maquette.development.values.model.governance.CheckWarning;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a model.
 */
@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Model {

    /**
     * Atomic values (properties) of a model.
     */
    ModelProperties properties;

    /**
     * Assigned user roles related to the model.
     */
    List<GrantedAuthorization<ModelMemberRole>> members;

    /**
     * Available permissions the current user has on the model.
     * This object instance is derived based on the requesting user.
     */
    ModelPermissions permissions;

    /**
     * Information about deployed services. The type of the services is not
     * specified here as the actual value is retrieved from model operations' module.
     */
    List<Object> services;

    /**
     * Creates a new instance based upon properties and additional required information.
     *
     * @param properties See {@link Model#properties}.
     * @param members See {@link Model#members}.
     * @param permissions See {@link Model#permissions}.
     * @param services See {@link Model#services}.
     * @return A new instance.
     */
    public static Model fromProperties(ModelProperties properties,
                                       List<GrantedAuthorization<ModelMemberRole>> members,
                                       ModelPermissions permissions,
                                       List<Object> services) {
        return apply(properties,
            members,
            permissions,
            services);
    }

    /**
     * Returns a model version based on its name.
     *
     * @param version The name the version (as tracked in MLflow).
     * @return The version if found.
     */
    public Optional<ModelVersion> findVersion(String version) {
        return properties
            .getVersions()
            .stream()
            .filter(v -> v
                .getVersion()
                .equals(version))
            .findAny();
    }

    /**
     * Returns number of exceptions in the latest version of the model.
     *
     * @return Number of exceptions.
     */
    @JsonProperty("exceptions")
    public long getExceptions() {
        return properties
            .getVersions()
            .stream()
            .max(Comparator.comparing(m -> m
                .getRegistered()
                .getAt()))
            .map(version -> {
                var count = version
                    .getCodeQualityChecks()
                    .stream()
                    .filter(r -> r instanceof CheckExemption)
                    .count();
                count += version
                    .getDataDependencyChecks()
                    .stream()
                    .filter(r -> r instanceof CheckExemption)
                    .count();

                return count;
            })
            .orElse(0L);
    }

    /**
     * Returns a number of warnings from the latest version of the model.
     *
     * @return Number of warnings.
     */
    @JsonProperty("warnings")
    public long getWarnings() {
        return properties
            .getVersions()
            .stream()
            .max(Comparator.comparing(m -> m
                .getRegistered()
                .getAt()))
            .map(version -> {
                var count = version
                    .getCodeQualityChecks()
                    .stream()
                    .filter(r -> r instanceof CheckWarning)
                    .count();
                count += version
                    .getDataDependencyChecks()
                    .stream()
                    .filter(r -> r instanceof CheckWarning)
                    .count();

                return count;
            })
            .orElse(0L);
    }
}
