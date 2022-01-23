package maquette.development.ports.infrastructure.docker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.development.ports.infrastructure.docker.model.DeploymentConfig;
import maquette.development.values.stacks.StackConfiguration;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeployedStackConfiguration {

    private static final String DEPLOYMENT_CONFIG = "deployment";
    private static final String STACK_CONFIGURATION = "stack";

    @JsonProperty(DEPLOYMENT_CONFIG)
    DeploymentConfig deploymentConfig;

    @JsonProperty(STACK_CONFIGURATION)
    StackConfiguration stackConfiguration;

    @JsonCreator
    public static DeployedStackConfiguration apply(
        @JsonProperty(DEPLOYMENT_CONFIG) DeploymentConfig deploymentConfig,
        @JsonProperty(STACK_CONFIGURATION) StackConfiguration stackConfiguration) {

        return new DeployedStackConfiguration(deploymentConfig, stackConfiguration);
    }

}
