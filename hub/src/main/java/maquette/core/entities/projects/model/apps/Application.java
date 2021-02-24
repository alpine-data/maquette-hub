package maquette.core.entities.projects.model.apps;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Application {

   UID id;

   String name;

   String secret;

   String description;

   String gitRepository;

   ActionMetadata created;

   ActionMetadata updated;

   @SuppressWarnings("unused")
   private Application() {
      this(UID.apply(), "", "", "", "", null, null);
   }

}
