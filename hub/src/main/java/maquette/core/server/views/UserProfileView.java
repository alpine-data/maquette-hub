package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserProfileView implements CommandResult {

   UserProfile profile;

   boolean isOwnProfile;

   List<ProjectProperties> projects;

   List<DataAssetProperties> dataAssets;

}