package maquette.core.entities.data.model.access;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.time.Instant;
import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class Granted implements DataAccessRequestEvent {

   ActionMetadata created;

   Instant until;

   String message;

   String environment;

   boolean downstreamApprovalRequired;

   @Override
   public Instant getEventMoment() {
      return created.getAt();
   }

   public Optional<String> getMessage() {
      return Optional.ofNullable(message);
   }

   public Optional<Instant> getUntil() {
      return Optional.ofNullable(until);
   }

}
