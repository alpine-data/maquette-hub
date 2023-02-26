package maquette.operations.value;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration for available states of a service instance. Maquette check availability of registered services.
 */
public enum DeployedModelServiceInstanceStatus {

    AVAILABLE("available"),
    NOT_AVAILABLE("not available");

    private final String value;

    DeployedModelServiceInstanceStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
