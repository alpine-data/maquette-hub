package maquette.datashop.values.access_requests.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.datashop.values.access_requests.DataAccessRequestState;
import maquette.datashop.values.access_requests.DataAccessRequestUserTriggeredEvent;

import java.time.Instant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Requested implements DataAccessRequestUserTriggeredEvent {

    private static final String CREATED = "created";
    private static final String REASON = "reason";

    @JsonProperty(CREATED)
    ActionMetadata created;

    @JsonProperty(REASON)
    String reason;

    @JsonCreator
    public static Requested apply(
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(REASON) String reason) {

        return new Requested(created, reason);
    }

    @Override
    public Instant getEventMoment() {
        return created.getAt();
    }

    @Override
    public DataAccessRequestState getNextState() {
        return DataAccessRequestState.REQUESTED;
    }
}
