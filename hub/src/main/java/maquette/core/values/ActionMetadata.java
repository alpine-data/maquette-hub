package maquette.core.values;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PUBLIC)
public class ActionMetadata {

    String by;

    Instant at;

    public static ActionMetadata apply(User by) {
        return apply(by, Instant.now());
    }

    public static ActionMetadata apply(String by) {
        return apply(by, Instant.now());
    }


    public static ActionMetadata apply(User user, Instant at) {
        return apply(user.getDisplayName(), at);
    }

}
