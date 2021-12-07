package maquette.development.values.sandboxes;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;

import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
public class Sandbox {

    UID id;

    UID project;

    UID volume;

    String name;

    ActionMetadata created;

    Set<StackInstanceParameters> stacks;

    @SuppressWarnings("unused")
    private Sandbox() {
        this(null, null, null, null, null, Sets.newHashSet());
    }

    public static Sandbox apply(UID id, UID project, UID volume, String name, ActionMetadata created) {
        return apply(id, project, volume, name, created, Sets.newHashSet());
    }

}
