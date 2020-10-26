package maquette.core.values.access;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class Expired implements DataAccessRequestEvent {

   Instant eventMoment;

}
