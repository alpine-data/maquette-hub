package maquette.datashop.providers.databases.ports;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.datashop.providers.databases.model.ConnectionTestResult;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.databases.model.FailedConnectionTestResult;
import maquette.datashop.providers.databases.model.SuccessfulConnectionTestResult;
import maquette.datashop.providers.datasets.records.Records;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class DatabaseJdbiImpl implements DatabasePort {

    @Override
    public CompletionStage<Records> read(
        DatabaseDriver driver, String connection, String username, String password, String query) {

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
    public CompletionStage<ConnectionTestResult> test(
        DatabaseDriver driver, String connection, String username, String password, String query) {

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
                e.printStackTrace();
                return FailedConnectionTestResult.apply(e.getMessage());
            }
        });
    }

}
