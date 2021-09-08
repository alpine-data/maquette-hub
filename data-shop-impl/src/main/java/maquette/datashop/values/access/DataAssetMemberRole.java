package maquette.datashop.values.access;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataAssetMemberRole {

    PRODUCER("producer"), CONSUMER("consumer"), MEMBER("member"), OWNER("owner"), STEWARD("sme");

    private final String value;

    DataAssetMemberRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
