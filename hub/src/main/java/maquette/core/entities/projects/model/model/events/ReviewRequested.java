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
public class ReviewRequested implements ModelVersionEvent, StateChangedEvent {

   ActionMetadata created;

   @Override
   public ModelVersionState getState() {
      return ModelVersionState.REVIEW_REQUESTED;
   }

}
