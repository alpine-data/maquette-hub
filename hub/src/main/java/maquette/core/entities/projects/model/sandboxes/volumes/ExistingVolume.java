package maquette.core.entities.projects.model.sandboxes.volumes;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class ExistingVolume implements VolumeDefinition {

   UID id;

   @SuppressWarnings("unused")
   private ExistingVolume() {
      this(UID.apply());
   }

}
