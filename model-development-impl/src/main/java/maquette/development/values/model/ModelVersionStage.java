package maquette.development.values.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enumeration represents the stage in which a model version can be.
 * These stages are derived from model stage value from MLflow.
 * <p>
 * See also https://mlflow.org/docs/latest/model-registry.html#transitioning-an-mlflow-models-stage.
 */
public enum ModelVersionStage {

    STAGING("Staging"), ARCHIVED("Archived"), PRODUCTION("Production"), NONE("None");

    private final String value;

    ModelVersionStage(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ModelVersionStage forValue(String value) {
        switch (value.toLowerCase()) {
            case "staging":
                return STAGING;
            case "archived":
                return ARCHIVED;
            case "production":
                return PRODUCTION;
            case "none":
                return NONE;
            default:
                throw new IllegalArgumentException(String.format("Cannot map value `%s` to ModelVersionStage.", value));
        }
    }

}
