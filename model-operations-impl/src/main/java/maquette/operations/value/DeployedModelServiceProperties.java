package maquette.operations.value;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedModelServiceProperties {

    String name;

    String gitRepositoryUrl;

    String backstageCatalogUrl;

    String azureDevopsPipelineUrl;

}
