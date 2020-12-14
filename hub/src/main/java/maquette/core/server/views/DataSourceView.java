package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasources.model.DataSource;
import maquette.core.server.CommandResult;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceView implements CommandResult {

   DataSource source;

   boolean canAccessData;

   boolean isOwner;

   boolean isMember;

}
