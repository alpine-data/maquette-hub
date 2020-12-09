package maquette.adapters.jdbc;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasources.model.ConnectionTestResult;
import maquette.core.entities.data.datasources.model.DataSourceDriver;
import maquette.core.entities.data.datasources.model.FailedConnectionTestResult;
import maquette.core.entities.data.datasources.model.SuccessfulConnectionTestResult;
import maquette.core.ports.JdbcPort;
import org.apache.avro.Schema;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class JdbcJdbiImpl implements JdbcPort {

   @Override
   public CompletionStage<ConnectionTestResult> test(DataSourceDriver driver, String connection, String username, String password, String query) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            var connectionString = String.format("%s:%s", driver.getConnectionPrefix(), connection);
            Jdbi jdbi = Jdbi.create(connectionString, username, password);

            return Operators.suppressExceptions(() -> jdbi.withHandle(handle -> handle
               .createQuery(query)
               .scanResultSet((resultSetSupplier, ctx) -> {
                  var count = 0;
                  var s = (Schema) null;
                  var rs = resultSetSupplier.get();
                  var cont = true;

                  while (cont) {
                     if (s == null) {
                        s = JdbcAvroHelper.createAvroSchema(rs);
                     }
                     count++;
                     cont = rs.next();
                  }

                  return SuccessfulConnectionTestResult.apply(s, count);
               })));
         } catch (Exception e) {
            return FailedConnectionTestResult.apply(e.getMessage());
         }
      });
   }

}
