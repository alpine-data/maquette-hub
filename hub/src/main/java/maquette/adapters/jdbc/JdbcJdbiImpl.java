package maquette.adapters.jdbc;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.values.data.records.Records;
import maquette.asset_providers.sources.model.ConnectionTestResult;
import maquette.asset_providers.sources.model.DataSourceDriver;
import maquette.asset_providers.sources.model.FailedConnectionTestResult;
import maquette.asset_providers.sources.model.SuccessfulConnectionTestResult;
import maquette.asset_providers.sources.ports.JdbcPort;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class JdbcJdbiImpl implements JdbcPort {

   @Override
   public CompletionStage<Records> read(DataSourceDriver driver, String connection, String username, String password, String query) {
      return CompletableFuture.supplyAsync(() -> {
         var connectionString = String.format("%s:%s", driver.getConnectionPrefix(), connection);
         Jdbi jdbi = Jdbi.create(connectionString, username, password);

         return Operators.suppressExceptions(() -> jdbi.withHandle(handle -> handle
            .createQuery(query)
            .scanResultSet((resultSetSupplier, ctx) -> {
               var rs = resultSetSupplier.get();

               if (!rs.next()) {
                  return Records.empty();
               } else {
                  var cont = true;
                  var schema = JdbcAvroHelper.createAvroSchema(rs);
                  var mappings = JdbcAvroHelper.createRecordMappers(rs);
                  var records = Lists.<GenericData.Record>newArrayList();

                  while (cont) {
                     records.add(JdbcAvroHelper.createRecord(rs, schema, mappings));
                     cont = rs.next();
                  }

                  return Records.fromRecords(records);
               }
            })));
      });
   }

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

                  return SuccessfulConnectionTestResult.apply(s, count - 1);
               })));
         } catch (Exception e) {
            return FailedConnectionTestResult.apply(e.getMessage());
         }
      });
   }

}
