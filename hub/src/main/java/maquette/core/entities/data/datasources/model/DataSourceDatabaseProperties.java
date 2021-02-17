package maquette.core.entities.data.datasources.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceDatabaseProperties {

   DataSourceDriver driver;

   String connection;

   String query;

   String username;

   String password;

}