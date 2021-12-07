package maquette.development.values.sandboxes;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
public class SandboxProperties {

   UID id;

   UID project;

   UID volume;

   String name;

   ActionMetadata created;

   Set<String> stacks;

   @SuppressWarnings("unused")
   private SandboxProperties() {
      this(null, null, null, null, null, Sets.newHashSet());
   }

   public static SandboxProperties apply(UID id, UID project, UID volume, String name, ActionMetadata created) {
      return apply(id, project, volume, name, created, Sets.newHashSet());
   }

}
