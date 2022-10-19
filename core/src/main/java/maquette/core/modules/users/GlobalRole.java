package maquette.core.modules.users;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GlobalRole {

    ADMIN("admin"), ADVANCED_USER("advanced-user");

    private final String value;

    GlobalRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
