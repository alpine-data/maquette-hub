package maquette.core.values.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnonymousUser implements User {

    List<String> roles;

    public static AnonymousUser apply(List<String> roles) {
        return new AnonymousUser(List.copyOf(roles));
    }

    public static AnonymousUser apply(String ...roles) {
        return apply(Arrays.asList(roles));
    }

    @Override
    public String getDisplayName() {
        return "anonymous";
    }

}
