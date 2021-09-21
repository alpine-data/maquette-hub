package maquette.core.values.authorization;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.annotation.Nullable;

/**
 * Helper class for APIs and Commands to specify authorizations with simple strings.
 * Requires {@link Authorizations#fromGenericAuthorizationDefinition(GenericAuthorizationDefinition)} to resolve to a
 * actural {@link Authorization} instance.
 */
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class GenericAuthorizationDefinition {

    String type;

    @Nullable
    String value;

    public static GenericAuthorizationDefinition apply(String type) {
        return apply(type, null);
    }

}
