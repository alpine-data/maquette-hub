package maquette.datashop.ports;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class Workspace {

    UID id;

    String name;

    String title;

}
