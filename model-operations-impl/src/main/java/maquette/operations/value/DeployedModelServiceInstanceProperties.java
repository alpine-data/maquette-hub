package maquette.operations.value;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedModelServiceInstanceProperties {

    String url;

    String modelVersion;

    String environment;

}
