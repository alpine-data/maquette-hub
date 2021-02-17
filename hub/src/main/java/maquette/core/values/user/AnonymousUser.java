package maquette.core.values.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.WildcardAuthorization;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Authorization toAuthorization() {
        return WildcardAuthorization.apply();
    }

    @Override
    public Optional<ProjectContext> getProjectContext() {
        return Optional.empty();
    }

    @Override
    public Optional<EnvironmentContext> getEnvironmentContext() {
        return Optional.empty();
    }

}
