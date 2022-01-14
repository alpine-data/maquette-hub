package maquette.datashop.providers.datasets.ports;

import lombok.AllArgsConstructor;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.values.AnalysisResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatasetDataExplorer implements DatasetDataExplorer {

    @Override
    public CompletionStage<AnalysisResult> analyze(
        String dataset, DatasetVersion version, String authTokenId, String authTokenSecret) {

        var result = AnalysisResult.empty(dataset, version.toString());
        return CompletableFuture.completedFuture(result);
    }
}
