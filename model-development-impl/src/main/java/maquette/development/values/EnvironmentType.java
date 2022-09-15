package maquette.development.values;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnvironmentType {

    EXTERNAL("external"), SANDBOX("sandbox");

    private final String value;

    EnvironmentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
