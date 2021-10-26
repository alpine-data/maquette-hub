package maquette.datashop.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.modules.users.model.UserProfile;
import maquette.core.server.commands.CommandResult;
import maquette.datashop.values.DataAsset;
import maquette.datashop.values.access.DataAssetPermissions;

import java.util.Map;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetView implements CommandResult {

    DataAsset asset;

    DataAssetPermissions permissions;

    Map<String, UserProfile> users;

}
