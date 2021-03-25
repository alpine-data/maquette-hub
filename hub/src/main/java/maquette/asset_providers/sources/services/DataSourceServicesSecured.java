package maquette.asset_providers.sources.services;

import lombok.AllArgsConstructor;
import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DataSourceServicesSecured implements DataSourceServices {

   private final DataSourceServices delegate;

   private final DataAssetCompanion companion;

   @Override
   public CompletionStage<Records> download(User executor, String source) {
      return companion
         .withAuthorization(
            () -> companion.hasPermission(executor, source, DataAssetPermissions::canConsume))
         .thenCompose(ok -> delegate.download(executor, source));
   }

   @Override
   public CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query) {
      return delegate.test(driver, connection, username, password, query);
   }

}
