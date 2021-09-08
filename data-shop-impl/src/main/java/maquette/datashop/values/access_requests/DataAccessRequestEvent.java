package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "event")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = Expired.class, name = "expired"),
        @JsonSubTypes.Type(value = Granted.class, name = "granted"),
        @JsonSubTypes.Type(value = Rejected.class, name = "rejected"),
        @JsonSubTypes.Type(value = Requested.class, name = "requested"),
        @JsonSubTypes.Type(value = Withdrawn.class, name = "withdrawn")
    })
public interface DataAccessRequestEvent {

    Instant getEventMoment();

}
