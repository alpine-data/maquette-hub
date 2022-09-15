package maquette.development.values.model.governance;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

import java.time.Instant;
import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataDependencies {

    /**
     * Moment when dependency check was executed.
     */
    Instant checked;

    /**
     * UIDs of dependent data assets.
     */
    List<UID> assets;

}
