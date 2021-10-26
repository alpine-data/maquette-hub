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
public class Granted implements DataAccessRequestUserTriggeredEvent {

    private static final String CREATED = "created";
    private static final String UNTIL = "until";
    private static final String MESSAGE = "message";
    private static final String ENVIRONMENT = "environment";
    private static final String DOWNSTREAM_APPROVAL_REQUIRED = "downstream-approval-required";

    @JsonProperty(CREATED)
    ActionMetadata created;

    @JsonProperty(UNTIL)
    Instant until;

    @JsonProperty(MESSAGE)
    String message;

    @JsonProperty(ENVIRONMENT)
    String environment;

    @JsonProperty(DOWNSTREAM_APPROVAL_REQUIRED)
    boolean downstreamApprovalRequired;

    @JsonCreator
    public static Granted apply(
        @JsonProperty(CREATED) ActionMetadata created,
        @JsonProperty(UNTIL) Instant until,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(ENVIRONMENT) String environment,
        @JsonProperty(DOWNSTREAM_APPROVAL_REQUIRED) boolean downstreamApprovalRequired) {

        return new Granted(created, until, message, environment, downstreamApprovalRequired);
    }

    @Override
    public Instant getEventMoment() {
        return created.getAt();
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public Optional<Instant> getUntil() {
        return Optional.ofNullable(until);
    }

}
