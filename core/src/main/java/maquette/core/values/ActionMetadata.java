package maquette.core.values;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.user.User;

import java.time.Instant;

/**
 * Simple value class which stores base information about actions (e.g. for modified or created fields).
 */
@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PUBLIC)
public class ActionMetadata {

    /**
     * The user who executed the action. The value should contain a unique, immutable user id.
     */
    String by;

    /**
     * The moment when the action was executed.
     */
    Instant at;

    /**
     * Creates a new instance.
     *
     * @param by The user who executed the action.
     * @return A new instance.
     */
    public static ActionMetadata apply(User by) {
        return apply(by, Instant.now());
    }

    /**
     * Creates a new instance.
     *
     * @param by The unique and immutable user id for the user who executed the action.
     * @return A new instance.
     */
    public static ActionMetadata apply(String by) {
        return apply(by, Instant.now());
    }

    /**
     * Creates a new instance.
     *
     * @param user The user who executed the action.
     * @param at The moment when the action was executed.
     * @return A new instance.
     */
    public static ActionMetadata apply(User user, Instant at) {
        return apply(user.getDisplayName(), at);
    }

}
