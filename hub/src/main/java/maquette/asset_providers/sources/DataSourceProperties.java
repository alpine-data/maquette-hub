package maquette.asset_providers.sources;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasources.model.DataSourceAccessType;
import maquette.core.entities.data.datasources.model.DataSourceDriver;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceProperties {

   DataSourceDriver driver;

   String connection;

   String query;

   String username;

   String password;

   DataSourceAccessType accessType;

}
