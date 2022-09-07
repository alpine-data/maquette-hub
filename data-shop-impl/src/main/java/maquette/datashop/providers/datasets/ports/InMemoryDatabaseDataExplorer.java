package maquette.datashop.providers.datasets.ports;

import lombok.AllArgsConstructor;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.databases.ports.DatabaseDataExplorer;
import maquette.datashop.providers.datasets.model.DatasetVersion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatabaseDataExplorer implements DatabaseDataExplorer {

    @Override
    public CompletionStage<DatabaseAnalysisResult> analyze(String database, String authTokenId,
                                                           String authTokenSecret) {
        var result = DatabaseAnalysisResult.empty(database);
        return CompletableFuture.completedFuture(result);
    }
}
