package maquette.core.values.access;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Rejected implements DataAccessRequestEvent {

   ActionMetadata created;

   String reason;

   @Override
   public Instant getEventMoment() {
      return created.getAt();
   }

}
