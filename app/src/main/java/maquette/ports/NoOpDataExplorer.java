package maquette.ports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.databases.ports.DatabaseDataExplorer;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.ports.AnalysisResult;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class NoOpDataExplorer implements DataExplorer {

    private static final Logger LOG = LoggerFactory.getLogger(NoOpDataExplorer.class);

    private final ObjectMapper om;


    @Override
    public CompletionStage<AnalysisResult> analyze(
        String dataset, DatasetVersion version, String authTokenId, String authTokenSecret) {

        return CompletableFuture.completedFuture(AnalysisResult.empty(dataset, version.toString()));
    }

    @Override
    public CompletionStage<DatabaseAnalysisResult> analyze(String database, String authTokenId,
                                                           String authTokenSecret) {

        return CompletableFuture.completedFuture(DatabaseAnalysisResult.empty(database));
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class DataExplorerRequest {

        @JsonProperty("type")
        String type;

        @JsonProperty("name")
        String name;

        @JsonProperty("additional_properties")
        Map<String, Object> additionalProperties;

        @JsonProperty("auth_token_id")
        String authTokenId;

        @JsonProperty("auth_token_secret")
        String authTokenSecret;

    }
}
