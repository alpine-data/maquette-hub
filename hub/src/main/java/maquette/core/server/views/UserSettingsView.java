package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.entities.users.model.UserSettings;
import maquette.core.server.CommandResult;

@Value
@AllArgsConstructor(staticName = "apply")
public class UserSettingsView implements CommandResult {

   UserProfile profile;

   UserSettings settings;

}
