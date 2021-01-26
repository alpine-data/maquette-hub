package maquette.core.values.data.logs;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAccessLogEntryProperties {

   UID asset;

   UID project;

   ActionMetadata accessed;

   String message;

   public static DataAccessLogEntryProperties apply(UID asset, ActionMetadata accessed, String message) {
      return apply(asset, null, accessed, message);
   }

   public Optional<UID> getProject() {
      return Optional.ofNullable(project);
   }

}
