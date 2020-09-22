package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeploymentConfig {

    String name;

    List<ContainerConfig> containers;

    public static DeploymentConfig apply(String name, ContainerConfig ...containers) {
        return apply(name, Arrays.asList(containers));
    }

}
