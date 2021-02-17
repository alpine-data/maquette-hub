package maquette.core.values.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE, staticName = "apply")
public class AuthenticatedUser implements User {

    String id;

    List<String> roles;

    EnvironmentContext environmentContext;

    ProjectContext projectContext;

    public static AuthenticatedUser apply(String id, List<String> roles) {
        return new AuthenticatedUser(id, List.copyOf(roles), null, null);
    }

    public static AuthenticatedUser apply(String id, String ...roles) {
        return apply(id, Arrays.asList(roles));
    }

    @Override
    public String getDisplayName() {
        return id;
    }

    @Override
    public Authorization toAuthorization() {
        return UserAuthorization.apply(id);
    }

    @Override
    public Optional<ProjectContext> getProjectContext() {
        return Optional.ofNullable(projectContext);
    }

    @Override
    public Optional<EnvironmentContext> getEnvironmentContext() {
        return Optional.ofNullable(environmentContext);
    }

    public AuthenticatedUser withProjectContext(ProjectContext context) {
        return apply(id, roles, environmentContext, context);
    }

    public AuthenticatedUser withEnvironmentContext(EnvironmentContext context) {
        return apply(id, roles, context, projectContext);
    }

    public AuthenticatedUser withRoles(List<String> roles) {
        return apply(id, roles);
    }

    public AuthenticatedUser withRoles(String ... roles) {
        return apply(id, roles);
    }

}
