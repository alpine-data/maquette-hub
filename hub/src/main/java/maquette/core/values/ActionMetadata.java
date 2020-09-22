package maquette.core.values;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class ActionMetadata {

    String by;

    Instant at;

}
