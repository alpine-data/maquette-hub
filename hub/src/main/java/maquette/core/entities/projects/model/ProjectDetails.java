package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectDetails {

    String id;

    String name;

    String title;

    String summary;

    ActionMetadata created;

    ActionMetadata modified;

    List<GrantedAuthorization> authorizations;

    public boolean isMember(User user) {
        return authorizations
           .stream()
           .anyMatch(auth -> auth.getAuthorization().isAuthorized(user));
    }

}
