package maquette.core.values.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
public class GrantedAuthorization<T extends Enum<T>> {

    private static final String GRANTED = "granted";
    private static final String AUTHORIZATION = "authorization";
    private static final String ROLE = "role";

    @JsonProperty
    ActionMetadata granted;

    @JsonProperty
    Authorization authorization;

    @JsonProperty
    T role;

    private GrantedAuthorization(ActionMetadata granted, Authorization authorization, T role) {
        this.granted = granted;
        this.authorization = authorization;
        this.role = role;
    }

    @JsonCreator
    public static <T extends Enum<T>> GrantedAuthorization<T> apply(@JsonProperty(GRANTED) ActionMetadata granted,
                                                                    @JsonProperty(AUTHORIZATION) Authorization authorization,
                                                                    @JsonProperty(ROLE) T role) {
        return new GrantedAuthorization<>(granted, authorization, role);
    }

    public boolean isEqualTo(GrantedAuthorization<T> other) {
        return other.authorization.equals(this.authorization) && role.equals(other.getRole());
    }

}
