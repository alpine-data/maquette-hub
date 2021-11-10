package maquette.datashop.values.access_requests.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.datashop.values.access_requests.DataAccessRequestState;

import java.time.Instant;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Expired implements DataAccessRequestEvent {

    private static final String MOMENT = "moment";

    @JsonProperty(MOMENT)
    Instant eventMoment;

    @JsonCreator
    public static Expired apply(@JsonProperty(MOMENT) Instant eventMoment) {
        return new Expired(eventMoment);
    }

    @Override
    public DataAccessRequestState getNextState() {
        return DataAccessRequestState.EXPIRED;
    }
}
