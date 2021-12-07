package maquette.development.values.model.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.development.values.model.ModelVersionState;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Rejected implements ModelVersionEvent, StateChangedEvent {

   ActionMetadata created;

   String reason;

   @Override
   public ModelVersionState getState() {
      return ModelVersionState.REJECTED;
   }

}
