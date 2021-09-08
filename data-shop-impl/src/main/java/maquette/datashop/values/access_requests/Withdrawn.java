package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.time.Instant;
import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Withdrawn implements DataAccessRequestEvent {

    private static final String CREATED = "created";
    private static final String REASON = "reason";

    @JsonProperty(CREATED)
    ActionMetadata created;

    @JsonProperty(REASON)
    String reason;

    @JsonCreator
    public static Withdrawn apply(
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(REASON) String reason) {

        return new Withdrawn(created, reason);
    }

    @Override
    public Instant getEventMoment() {
        return created.getAt();
    }

    public Optional<String> getReason() {
        return Optional.ofNullable(reason);
    }

}
