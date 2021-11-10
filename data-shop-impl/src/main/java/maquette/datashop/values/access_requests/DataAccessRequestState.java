package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAccessRequestState {
    REQUESTED("requested"),
    GRANTED("granted"),
    REJECTED("rejected"),
    EXPIRED("expired"),
    REVIEW_REQUIRED("review-required"),
    WITHDRAWN("withdrawn");

    @JsonValue
    private final String value;

    DataAccessRequestState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
