package maquette.core.modules.users.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class UserNotification {

    String id;

    Instant sent;

    boolean read;

    String message;

}
