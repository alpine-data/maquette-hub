package maquette.datashop.providers.databases.ports;

import lombok.AllArgsConstructor;
import maquette.datashop.providers.databases.model.ConnectionTestResult;
import maquette.datashop.providers.databases.model.DatabaseDriver;
import maquette.datashop.providers.datasets.records.Records;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class InMemoryDatabasePortImpl implements DatabasePort {

    @Override
    public CompletionStage<Records> read(DatabaseDriver driver, String connection, String username, String password,
                                         String query) {
        return null;
    }

    @Override
    public CompletionStage<ConnectionTestResult> test(DatabaseDriver driver, String connection, String username,
                                                      String password, String query) {
        return null;
    }
}
