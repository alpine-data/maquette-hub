package maquette.datashop.providers.databases.ports;

import maquette.datashop.providers.databases.model.ConnectionTestResult;
import maquette.datashop.providers.databases.model.DataSourceDriver;
import maquette.datashop.providers.datasets.records.Records;

import java.util.concurrent.CompletionStage;

public interface DatabasePort {

    CompletionStage<Records> read(DataSourceDriver driver, String connection, String username, String password, String query);

    CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query);

}
