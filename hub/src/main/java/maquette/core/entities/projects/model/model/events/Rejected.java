package maquette.core.entities.projects.model.model.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.model.ModelVersionState;
import maquette.core.values.ActionMetadata;

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
