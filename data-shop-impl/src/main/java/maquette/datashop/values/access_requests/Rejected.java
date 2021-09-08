package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.time.Instant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Rejected implements DataAccessRequestEvent {

    private static final String CREATED = "created";
    private static final String REASON = "reason";

    @JsonProperty(CREATED)
    ActionMetadata created;

    @JsonProperty(REASON)
    String reason;

    @JsonCreator
    public static Rejected apply(@JsonProperty(CREATED) ActionMetadata created, @JsonProperty(REASON) String reason) {
        return new Rejected(created, reason);
    }

    @Override
    public Instant getEventMoment() {
        return created.getAt();
    }

}
