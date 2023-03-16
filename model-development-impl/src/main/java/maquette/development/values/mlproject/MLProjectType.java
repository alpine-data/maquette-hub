package maquette.development.values.mlproject;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MLProjectType {

    DEFAULT("default"),
    NATURAL_LANGUAGE_PROCESSING("nlp");

    private final String value;

    MLProjectType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
