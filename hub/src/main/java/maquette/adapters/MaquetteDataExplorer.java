package maquette.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.asset_providers.datasets.DatasetDataExplorer;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.sources.ports.DataSourceDataExplorer;
import maquette.common.Operators;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteDataExplorer implements DatasetDataExplorer, DataSourceDataExplorer {

   private static final Logger LOG = LoggerFactory.getLogger(DatasetDataExplorer.class);

   ObjectMapper om;

   OkHttpClient client;

   public static MaquetteDataExplorer apply(ObjectMapper om) {
      OkHttpClient client = new OkHttpClient.Builder()
         .readTimeout(3, TimeUnit.MINUTES)
         .build();
      return apply(om, client);
   }

   @Override
   public CompletionStage<JsonNode> analyze(String dataset, DatasetVersion version) {
      var json = Operators.suppressExceptions(() ->
         om.writeValueAsString(AnalyzeDatasetRequest.apply(dataset, version.toString())));
      return sendRequest(json);
   }

   @Override
   public CompletionStage<JsonNode> analyze(String source) {
      var json = Operators.suppressExceptions(() -> om.writeValueAsString(AnalyzeDataSourceRequest.apply(source)));
      return sendRequest(json);
   }

   private CompletionStage<JsonNode> sendRequest(String json) {
      return CompletableFuture.supplyAsync(() -> {
         var request = new Request.Builder()
            .url("http://127.0.0.1:9085/api/statistics?plots=true")
            .post(RequestBody.create(json, MediaType.parse("application/json")))
            .build();

         try {
            var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

            if (!response.isSuccessful()) {
               var body = response.body();
               var content = body != null ? Operators.suppressExceptions(body::string) : "";
               content = StringUtils.leftPad(content, 3);
               LOG.warn("Received non-successful response from analysis service:\n" + content);

               return Operators.suppressExceptions(() -> om.readValue("{}", JsonNode.class));
            } else {
               var body = response.body();
               var content = body != null ? Operators.suppressExceptions(body::string) : "{}";
               return Operators.suppressExceptions(() -> om.readValue(content, JsonNode.class));
            }
         } catch (Exception e) {
            LOG.warn("Exception occurred while calling Maquette Data Explorer.", e);
            return Operators.suppressExceptions(() -> om.readValue("{}", JsonNode.class));
         }
      });
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class AnalyzeDatasetRequest {

      String dataset;

      String version;

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class AnalyzeDataSourceRequest {

      String source;

   }

}
