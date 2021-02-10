package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserProfileView implements CommandResult {

   UserProfile profile;

   boolean isOwnProfile;

   List<ProjectProperties> projects;

   List<DataAssetProperties<?>> dataAssets;

}
