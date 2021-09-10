package maquette.core.modules.users.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.UID;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserNotification {

    private static final String ID = "id";
    private static final String SENT = "sent";
    private static final String READ = "read";
    private static final String MESSAGE = "message";

    @JsonProperty(ID)
    UID id;

    @JsonProperty(SENT)
    Instant sent;

    @JsonProperty(READ)
    boolean read;

    @JsonProperty(MESSAGE)
    String message;

    @JsonCreator
    public static UserNotification apply(@JsonProperty(ID) UID id, @JsonProperty(SENT) Instant sent,
                                         @JsonProperty(READ) boolean read, @JsonProperty(MESSAGE) String message) {
        return new UserNotification(id, sent, read, message);
    }

    public static UserNotification fake(String message) {
        return apply(UID.apply(), Instant.now(), false, message);
    }

    public static UserNotification fake() {
        return fake("fake");
    }
}
