package maquette.core.values.authorization;

import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
public class GrantedAuthorization<T extends Enum<T>> {

    ActionMetadata granted;

    Authorization authorization;

    T role;

    private GrantedAuthorization(ActionMetadata granted, Authorization authorization, T role) {
        this.granted = granted;
        this.authorization = authorization;
        this.role = role;
    }

    public static <T extends Enum<T>> GrantedAuthorization<T> apply(ActionMetadata granted, Authorization authorization, T role) {
        return new GrantedAuthorization<>(granted, authorization, role);
    }

}
