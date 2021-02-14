package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDataAssetView implements CommandResult {

   String steward;

   List<UserProfile> users;

   String owner;

}
