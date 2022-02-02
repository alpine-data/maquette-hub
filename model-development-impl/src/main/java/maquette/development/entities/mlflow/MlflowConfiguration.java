package maquette.development.entities.mlflow;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class MlflowConfiguration {

    String internalTrackingUrl;

    String mlflowBasePath;

}
