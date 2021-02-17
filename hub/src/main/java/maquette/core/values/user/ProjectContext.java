package maquette.core.values.user;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectContext {

   UID id;

   String name;

}
