package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeploymentConfig {

    String name;

    List<ContainerConfig> containers;

    public static DeploymentConfigBuilder builder(String name) {
        return DeploymentConfigBuilder.apply(name);
    }

}
