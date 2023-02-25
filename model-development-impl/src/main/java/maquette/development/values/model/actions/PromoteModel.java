package maquette.development.values.model.actions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.development.values.model.ModelVersionStage;

/**
 * See {@link ModelAction} for description of the purpose.
 */
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class PromoteModel implements ModelAction {

    /**
     * The stage to which the model version can be promoted.
     */
    ModelVersionStage to;

}
