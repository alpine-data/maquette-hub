package maquette.development.ports.infrastructure.docker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeploymentProperties {

    private static final String CONFIG = "config";
    private static final String PROPERTIES = "properties";
    private static final String CREATED = "created";
    private static final String STATUS = "status";

    @JsonProperty(CONFIG)
    DeploymentConfig config;

    @JsonProperty(PROPERTIES)
    List<ContainerProperties> properties;

    @JsonProperty(CREATED)
    Instant created;

    @JsonProperty(STATUS)
    DeploymentStatus status;

    @JsonCreator
    public static DeploymentProperties apply(
        @JsonProperty(CONFIG) DeploymentConfig config,
        @JsonProperty(PROPERTIES) List<ContainerProperties> properties,
        @JsonProperty(CREATED) Instant created,
        @JsonProperty(STATUS) DeploymentStatus status) {

        return new DeploymentProperties(config, properties, created, status);
    }

}
