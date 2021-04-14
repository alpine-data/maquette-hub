package maquette.asset_providers.sources.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceSettings {

   DataSourceDriver driver;

   String connection;

   String query;

   String username;

   String password;

   String schedule;

   DataSourceAccessType accessType;

}
