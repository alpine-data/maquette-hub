package maquette.datashop.providers.databases.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatabaseSettings {

        DataSourceDriver driver;

        String connection;

        String query;

        String username;

        String password;

}
