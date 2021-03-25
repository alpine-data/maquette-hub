package maquette.asset_providers.sources.services;

import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface DataSourceServices {

   CompletionStage<Records> download(User executor, String dataSource);

   CompletionStage<ConnectionTestResult> test(
      DataSourceDriver driver, String connection, String username, String password, String query);

}