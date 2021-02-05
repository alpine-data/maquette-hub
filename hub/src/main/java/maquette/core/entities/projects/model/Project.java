package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.sandboxes.model.Sandbox;
import maquette.core.entities.sandboxes.model.stacks.StackProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAsset;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class Project {

    UID id;

    String name;

    String title;

    String summary;

    String mlflowBaseUrl;

    ActionMetadata created;

    ActionMetadata modified;

    List<DataAccessRequest> dataAccessRequests;

    List<GrantedAuthorization<ProjectMemberRole>> members;

    List<DataAsset<?>> assets;

    List<Sandbox> sandboxes;

    List<StackProperties> stacks;

    public boolean isMember(User user, ProjectMemberRole role) {
        return members
           .stream()
           .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role)));
    }

    public boolean isMember(User user) {
        return isMember(user, null);
    }

}
