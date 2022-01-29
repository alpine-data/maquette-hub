package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StackInstanceStatus {

    INITIALIZED("initialized"),
    IN_PROGRESS("in-progress"),
    DEPLOYED("deployed"),
    FAILED("failed");

    private final String value;

    StackInstanceStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static StackInstanceStatus forValue(String value) {
        if (value.equals(INITIALIZED.getValue())) {
            return INITIALIZED;
        } else if (value.equals(IN_PROGRESS.getValue())) {
            return IN_PROGRESS;
        } else if (value.equals(DEPLOYED.getValue())) {
            return DEPLOYED;
        } else if (value.equals(FAILED.getValue())) {
            return FAILED;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
