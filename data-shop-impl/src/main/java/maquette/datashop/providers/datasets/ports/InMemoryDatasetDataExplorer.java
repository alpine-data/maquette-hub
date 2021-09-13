package maquette.datashop.providers.datasets.ports;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.datashop.providers.datasets.model.DatasetVersion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatasetDataExplorer implements DatasetDataExplorer {

    @Override
    public CompletionStage<JsonNode> analyze(String dataset, DatasetVersion version) {
        var result = DefaultObjectMapperFactory.apply().createJsonMapper(true).createObjectNode();
        return CompletableFuture.completedFuture(result);
    }

}
