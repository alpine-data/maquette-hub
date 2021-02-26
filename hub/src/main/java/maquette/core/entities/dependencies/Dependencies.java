package maquette.core.entities.dependencies;

import akka.Done;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.common.Templates;
import maquette.core.entities.dependencies.model.*;
import maquette.core.entities.dependencies.neo4j.Graph;
import maquette.core.entities.dependencies.neo4j.QueryRequest;
import maquette.core.entities.dependencies.neo4j.QueryResponse;
import maquette.core.entities.dependencies.neo4j.Result;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply")
public final class Dependencies {

   private static final Logger LOG = LoggerFactory.getLogger(Dependencies.class);

   private final Jdbi jdbi;

   private final ObjectMapper om;

   private final ObjectMapper httpOM;

   private final OkHttpClient client;

   public static Dependencies apply() {
      var jdbi = Jdbi.create("jdbc:neo4j:bolt://localhost:7687", "neo4j", "password");
      var om = ObjectMapperFactory.apply().create(false);
      var client = new OkHttpClient.Builder()
         .readTimeout(3, TimeUnit.MINUTES)
         .build();

      om.disable(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature());

      return apply(jdbi, om, ObjectMapperFactory.apply().create(true), client);
   }

   public CompletionStage<Graph<DependencyNode>> getDependencyGraph(DataAssetNode dataAsset) {
      var stmt = Templates.renderTemplateFromResources(
         "cyphers/get-dependencies-data.txt",
         Map.of("dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset))));

      return CompletableFuture.completedFuture(query(stmt));
   }

   public CompletionStage<Done> trackConsumption(DataAssetNode dataAsset, ApplicationNode application) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-consumption-app.txt",
            Map.of(
               "dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset)),
               "application", Operators.suppressExceptions(() -> om.writeValueAsString(application))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> trackConsumption(DataAssetNode dataAsset, ModelNode model) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-consumption-model.txt",
            Map.of(
               "dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset)),
               "model", Operators.suppressExceptions(() -> om.writeValueAsString(model))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> trackConsumption(DataAssetNode dataAsset, UserNode user, ProjectNode project) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-consumption-project.txt",
            Map.of(
               "dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset)),
               "user", Operators.suppressExceptions(() -> om.writeValueAsString(user)),
               "project", Operators.suppressExceptions(() -> om.writeValueAsString(project))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> trackProduction(DataAssetNode dataAsset, ApplicationNode application) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-production-app.txt",
            Map.of(
               "dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset)),
               "application", Operators.suppressExceptions(() -> om.writeValueAsString(application))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> trackProduction(DataAssetNode dataAsset, UserNode user) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-consumption-user.txt",
            Map.of(
               "dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset)),
               "user", Operators.suppressExceptions(() -> om.writeValueAsString(user))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> trackProduction(DataAssetNode dataAsset, UserNode user, ProjectNode project) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-production-project.txt",
            Map.of(
               "dataAsset", Operators.suppressExceptions(() -> om.writeValueAsString(dataAsset)),
               "project", Operators.suppressExceptions(() -> om.writeValueAsString(project)),
               "user", Operators.suppressExceptions(() -> om.writeValueAsString(user))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   public CompletionStage<Done> trackUsage(ModelNode model, ApplicationNode application) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-usage-app.txt",
            Map.of(
               "model", Operators.suppressExceptions(() -> om.writeValueAsString(model)),
               "application", Operators.suppressExceptions(() -> om.writeValueAsString(application))))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @SuppressWarnings("unchecked")
   private Graph<DependencyNode> query(String query) {
      var requestJson = Operators.suppressExceptions(() -> httpOM.writeValueAsString(QueryRequest.apply(query)));

      var request = new Request.Builder()
         .url("http://localhost:7474/db/neo4j/tx/commit")
         .header("Authorization", Credentials.basic("neo4j", "password"))
         .post(RequestBody.create(requestJson, MediaType.parse("application/json")))
         .build();

      var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

      if (!response.isSuccessful()) {
         var body = response.body();
         var content = body != null ? Operators.suppressExceptions(body::string) : "";
         content = StringUtils.leftPad(content, 3);
         LOG.warn("Received error response from Neo4J HTTP API:\n" + content);

         return Graph.apply();
      } else {
         var body = response.body();
         var responseJson = body != null ? Operators.suppressExceptions(body::string) : "{}";
         var typeRef = om.getTypeFactory().constructParametricType(QueryResponse.class, DependencyNode.class);
         var result = Operators.suppressExceptions(() -> (QueryResponse<DependencyNode>) om.readValue(responseJson, typeRef));

         return result
            .getResults()
            .stream()
            .findFirst()
            .map(Result::getData)
            .map(r -> r
               .stream()
               .reduce(
                  Graph.<DependencyNode>apply(),
                  (g, rs) -> g.combine(rs.getGraph()),
                  Graph::combine))
            .orElse(Graph.apply());
      }
   }

}
