package maquette.core.values.authorization;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class GrantedAuthorization {

    ActionMetadata granted;

    Authorization authorization;

}
