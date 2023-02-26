package maquette.operations.value;

import lombok.*;

import java.util.Set;

/**
 * This value class contains properties of a deployed model service instance. Each service may have multiple instances.
 * The values are sent to Maquette by the corresponding DevOps system (e.g., Azure DevOps Pipelines) during deployment
 * of a service.
 */
@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class RegisterDeployedModelServiceInstanceParameters {

    /**
     * The URL of the service (API). This is used as unique identifier for service instances.
     */
    String url;

    /**
     * Aa set of deployed models (including version) within the service.
     */
    Set<DeployedModelVersion> models;

    /**
     * The name of the environment to which it is deployed.
     */
    String environment;

}
