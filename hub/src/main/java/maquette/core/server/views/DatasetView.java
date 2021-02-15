package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.logs.DataAccessLogEntry;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetView implements CommandResult {

   Dataset dataset;

   List<DataAccessLogEntry> logs;

   DataAssetPermissions permissions;

   List<UserProfile> owners;

   List<UserProfile> stewards;

}
