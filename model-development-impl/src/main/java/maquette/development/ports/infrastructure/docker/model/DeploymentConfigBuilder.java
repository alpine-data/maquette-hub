package maquette.development.ports.infrastructure.docker.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class DeploymentConfigBuilder {

    String name;

    List<ContainerConfig> containers;

    public static DeploymentConfigBuilder apply(String name) {
        return apply(name, Lists.newArrayList());
    }

    public DeploymentConfigBuilder withContainerConfig(ContainerConfig containerConfig) {
        containers.add(containerConfig);
        return this;
    }

    public DeploymentConfig build() {
        return DeploymentConfig.apply(name, List.copyOf(containers));
    }

}
