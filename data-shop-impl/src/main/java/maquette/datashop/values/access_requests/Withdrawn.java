package maquette.datashop.values.access_requests;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.time.Instant;
import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
public class Withdrawn implements DataAccessRequestEvent {

   ActionMetadata created;

   String reason;

   @Override
   public Instant getEventMoment() {
      return created.getAt();
   }

   public Optional<String> getReason() {
      return Optional.ofNullable(reason);
   }

}
