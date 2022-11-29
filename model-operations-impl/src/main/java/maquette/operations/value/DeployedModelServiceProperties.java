package maquette.operations.value;

import lombok.*;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DeployedModelServiceProperties {

    String name;

    String gitRepositoryUrl;

    String backstageCatalogUrl;

    String azureDevopsPipelineUrl;

}
