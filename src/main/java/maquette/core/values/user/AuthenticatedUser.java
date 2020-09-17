package maquette.core.values.user;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUser implements User {

    String id;

    List<String> roles;

    public static AuthenticatedUser apply(String id, List<String> roles) {
        return new AuthenticatedUser(id, List.copyOf(roles));
    }

    public static AuthenticatedUser apply(String id, String ...roles) {
        return apply(id, Arrays.asList(roles));
    }

    @Override
    public String getDisplayName() {
        return id;
    }

    public AuthenticatedUser withRoles(List<String> roles) {
        return apply(id, roles);
    }

    public AuthenticatedUser withRoles(String ... roles) {
        return apply(id, roles);
    }

}
