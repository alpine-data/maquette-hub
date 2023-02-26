package maquette.operations.value;

import lombok.*;

/**
 * Value class which contains properties of a model service. The values are posted to Maquette by a DevOps system
 * (e.g., Azure Pipelines during deployment).
 */
@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RegisterDeployedModelServiceParameters {

    /**
     * The unique name of the service.
     */
    String name;

    /**
     * The Git Repository URL for the source code of the Service's source code.
     */
    String gitRepositoryUrl;

    /**
     * The catalog URL for the service in Backstage.
     */
    String backstageCatalogUrl;

}
