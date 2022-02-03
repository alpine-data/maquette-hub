package maquette.ports;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;
import maquette.core.config.Configs;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.databases.ports.DatabaseDataExplorer;
import maquette.datashop.providers.datasets.Datasets;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import maquette.datashop.providers.datasets.ports.AnalysisResult;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteDataExplorer implements DatasetDataExplorer, DatabaseDataExplorer {

    private static final Logger LOG = LoggerFactory.getLogger(MaquetteDataExplorer.class);

    private final ObjectMapper om;

    private final String baseUrl;

    private final OkHttpClient client;

    public static MaquetteDataExplorer apply(ObjectMapper om, String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .build();

        return apply(om, baseUrl, client);
    }

    public static MaquetteDataExplorer apply(ObjectMapper om) {
        var url = Configs.application.getString("maquette.data-explorer.url");
        return apply(om, url);
    }

    @Override
    public CompletionStage<AnalysisResult> analyze(
        String dataset, DatasetVersion version, String authTokenId, String authTokenSecret) {

        return CompletableFuture.supplyAsync(() -> {
            var additionalProperties = Maps.<String, Object>newHashMap();
            additionalProperties.put("version", version);

            var requestBody = DataExplorerRequest.apply(
                Datasets.TYPE_NAME, dataset, additionalProperties, authTokenId, authTokenSecret);

            var json = Operators.suppressExceptions(() -> om.writeValueAsString(requestBody));

            var request = new Request.Builder()
                .url(String.format("%s/api/statistics", baseUrl))
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

            try {
                var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

                if (!response.isSuccessful()) {
                    var body = response.body();
                    var content = body != null ? Operators.suppressExceptions(body::string) : "";
                    content = StringUtils.leftPad(content, 3);
                    LOG.warn("Received non-successful response from analysis service:\n" + content);

                    return AnalysisResult.empty(dataset, version.toString());
                } else {
                    var body = response.body();
                    var content = body != null ? Operators.suppressExceptions(body::string) : "{}";
                    return Operators.suppressExceptions(() -> om.readValue(content, AnalysisResult.class));
                }
            } catch (Exception e) {
                LOG.warn("Exception occurred while calling Maquette Data Explorer.", e);
                return AnalysisResult.empty(dataset, version.toString());
            }
        });
    }

    @Override
    public CompletionStage<DatabaseAnalysisResult> analyze(String database, String query, String authTokenId,
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
