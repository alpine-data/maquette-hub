package maquette.workspaces.api;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class WorkspaceProperties {

    UID id;

    String name;

    List<String> members;

    public static WorkspaceProperties apply(String name) {
        return apply(UID.apply(), name, Lists.newArrayList());
    }

}
