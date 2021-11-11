package maquette.datashop.values.access_requests.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.datashop.values.access_requests.DataAccessRequestState;

import java.time.Instant;
import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Approved implements DataAccessRequestEvent {

    private static final String CREATED = "created";
    private static final String MESSAGE = "message";

    @JsonProperty(CREATED)
    ActionMetadata created;

    @JsonProperty(MESSAGE)
    String message;

    @JsonCreator
    public static Approved apply(
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(MESSAGE) String message) {

        return new Approved(created, message);
    }

    @Override
    public Instant getEventMoment() {
        return created.getAt();
    }

    @Override
    public DataAccessRequestState getNextState() {
        return DataAccessRequestState.GRANTED;
    }

    Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

}
