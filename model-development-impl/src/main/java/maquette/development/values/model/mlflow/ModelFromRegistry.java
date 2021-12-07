package maquette.development.values.model.mlflow;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelFromRegistry {

   String name;

   Instant created;

   Instant updated;

   List<VersionFromRegistry> versions;

}
