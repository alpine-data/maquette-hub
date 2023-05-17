package maquette.development.values;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.development.values.stacks.StackRuntimeState;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class WorkSpaceMlflowView {
    StackRuntimeState mlflowStatus;
}
