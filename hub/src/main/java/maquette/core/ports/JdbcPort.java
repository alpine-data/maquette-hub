package maquette.core.ports;

import maquette.core.entities.data.datasources.model.ConnectionTestResult;
import maquette.core.entities.data.datasources.model.DataSourceDriver;

import java.util.concurrent.CompletionStage;

public interface JdbcPort {

   CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query);

}
