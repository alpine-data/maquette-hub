package maquette.asset_providers.sources.ports;

import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.core.values.data.records.Records;

import java.util.concurrent.CompletionStage;

public interface JdbcPort {

   CompletionStage<Records> read(DataSourceDriver driver, String connection, String username, String password, String query);

   CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query);

}
