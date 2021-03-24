package maquette.asset_providers.sources.services;

import maquette.core.entities.data.datasources.model.ConnectionTestResult;
import maquette.core.entities.data.datasources.model.DataSourceDriver;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface DataSourceServices {

   CompletionStage<Records> download(User executor, String dataSource);

   CompletionStage<ConnectionTestResult> test(
      DataSourceDriver driver, String connection, String username, String password, String query);

}
