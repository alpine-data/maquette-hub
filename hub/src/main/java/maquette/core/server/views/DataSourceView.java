package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasources.model.DataSource;
import maquette.core.entities.dependencies.neo4j.Graph;
import maquette.core.entities.logs.LogEntry;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;
import maquette.core.services.dependencies.model.DependencyPropertiesNode;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.logs.DataAccessLogEntry;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceView implements CommandResult {

   DataSource source;

   List<LogEntry> logs;

   DataAssetPermissions permissions;

   List<UserProfile> owners;

   List<UserProfile> stewards;

   Graph<DependencyPropertiesNode> dependencies;

}
