package maquette.core.entities.projects.model.model.governance;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.model.DataAssetProperties;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataDependencies {

   Instant checked;

   List<DataAssetProperties> assets;

}
