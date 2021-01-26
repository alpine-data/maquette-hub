package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasources.model.DataSource;
import maquette.core.server.CommandResult;
import maquette.core.values.data.logs.DataAccessLogEntry;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceView implements CommandResult {

   DataSource source;

   List<DataAccessLogEntry> logs;

   boolean canAccessData;

   boolean isOwner;

   boolean isMember;

}
