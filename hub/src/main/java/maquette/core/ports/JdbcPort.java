package maquette.core.ports;

import maquette.core.values.data.records.Records;
import maquette.core.entities.data.datasources.model.ConnectionTestResult;
import maquette.core.entities.data.datasources.model.DataSourceDatabaseProperties;
import maquette.core.entities.data.datasources.model.DataSourceDriver;

import java.util.concurrent.CompletionStage;

public interface JdbcPort {

   default CompletionStage<Records> read(DataSourceDatabaseProperties properties) {
      return read(properties.getDriver(), properties.getConnection(), properties.getUsername(), properties.getPassword(), properties.getQuery());
   }

   CompletionStage<Records> read(DataSourceDriver driver, String connection, String username, String password, String query);

   default CompletionStage<ConnectionTestResult> test(DataSourceDatabaseProperties properties) {
      return test(properties.getDriver(), properties.getConnection(), properties.getUsername(), properties.getPassword(), properties.getQuery());
   }

   CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query);

}
