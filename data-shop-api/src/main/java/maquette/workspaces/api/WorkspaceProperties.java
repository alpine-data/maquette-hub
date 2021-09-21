package maquette.workspaces.api;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class WorkspaceProperties {

    UID id;

    String name;

    public static WorkspaceProperties apply(String name) {
        return apply(UID.apply(), name);
    }

}
