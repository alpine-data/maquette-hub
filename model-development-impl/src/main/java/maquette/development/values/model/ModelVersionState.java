package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModelVersionState {

    REGISTERED("registered"), REVIEW_REQUESTED("requested"), APPROVED("approved"), REJECTED("rejected");

    private final String value;

    ModelVersionState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
