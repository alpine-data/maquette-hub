package maquette.datashop.values.metadata;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Timeliness {

    HOURLY("hourly"), DAILY("daily"), WEEKLY("weekly"), MONTHLY("monthly"), YEARLY("yearly");

    private final String value;

    Timeliness(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
