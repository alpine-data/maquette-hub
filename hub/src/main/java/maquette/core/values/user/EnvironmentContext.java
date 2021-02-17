package maquette.core.values.user;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class EnvironmentContext {

   EnvironmentType type;

   String name;

}
