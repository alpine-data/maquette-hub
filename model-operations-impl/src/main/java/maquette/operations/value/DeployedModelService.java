package maquette.operations.value;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;


@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DeployedModelService {

    DeployedModelServiceProperties properties;

    List<DeployedModelInstance> instances;

}
