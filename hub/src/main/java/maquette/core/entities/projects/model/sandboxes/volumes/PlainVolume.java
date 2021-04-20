package maquette.core.entities.projects.model.sandboxes.volumes;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class PlainVolume implements NewVolume {

   String name;

   @SuppressWarnings("unused")
   private PlainVolume() {
      this("");
   }

}
