package maquette.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.common.Operators;
import maquette.core.ports.DataExplorer;
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
public final class MaquetteDataExplorer implements DataExplorer {

   private static final Logger LOG = LoggerFactory.getLogger(DataExplorer.class);

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
      return CompletableFuture.supplyAsync(() -> {
         var json = Operators.suppressExceptions(() ->
            om.writeValueAsString(AnalyzeRequest.apply(dataset, version.toString())));

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
            LOG.warn("Exception occurred while calling Maquette Data Explorer.");
            return Operators.suppressExceptions(() -> om.readValue("{}", JsonNode.class));
         }
      });
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class AnalyzeRequest {

      String dataset;

      String version;

   }

}
