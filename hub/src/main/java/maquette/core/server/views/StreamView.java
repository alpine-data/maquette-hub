package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.logs.DataAccessLogEntry;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class StreamView implements CommandResult {

   Stream stream;

   List<DataAccessLogEntry> logs;

   DataAssetPermissions permissions;

   List<UserProfile> owners;

   List<UserProfile> stewards;

}
