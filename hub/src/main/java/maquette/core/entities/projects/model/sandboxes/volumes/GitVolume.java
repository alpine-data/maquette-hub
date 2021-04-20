package maquette.core.entities.projects.model.sandboxes.volumes;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class GitVolume implements NewVolume {

   String name;

   String repository;

   @SuppressWarnings("unused")
   private GitVolume() {
      this("", "");
   }

}
