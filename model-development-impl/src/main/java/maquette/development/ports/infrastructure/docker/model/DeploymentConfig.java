package maquette.development.ports.infrastructure.docker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeploymentConfig {

    private static final String NAME = "name";
    private static final String CONTAINERS = "containers";

    @JsonProperty(NAME)
    String name;

    @JsonProperty(CONTAINERS)
    List<ContainerConfig> containers;

    @JsonCreator
    public static DeploymentConfig apply(
        @JsonProperty(NAME) String name,
        @JsonProperty(CONTAINERS) List<ContainerConfig> containers) {

        return new DeploymentConfig(name, containers);
    }

    public static DeploymentConfigBuilder builder(String name) {
        return DeploymentConfigBuilder.apply(name);
    }

}
