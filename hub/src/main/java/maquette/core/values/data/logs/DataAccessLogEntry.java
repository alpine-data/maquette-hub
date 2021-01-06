package maquette.core.values.data.logs;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.data.DataAssetProperties;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAccessLogEntry {

   DataAssetProperties<?> asset;

   ProjectProperties project;

   ActionMetadata accessed;

   String message;

   public static DataAccessLogEntry apply(DataAssetProperties<?> asset, ActionMetadata accessed, String message) {
      return apply(asset, null, accessed, message);
   }

   public Optional<ProjectProperties> getProject() {
      return Optional.ofNullable(project);
   }

}
