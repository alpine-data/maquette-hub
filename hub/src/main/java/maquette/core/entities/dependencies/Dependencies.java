package maquette.core.entities.dependencies;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Templates;
import maquette.core.entities.dependencies.model.*;
import org.jdbi.v3.core.Jdbi;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Dependencies {

   private final Jdbi jdbi;

   public static Dependencies apply() {
      var jdbi = Jdbi.create("jdbc:neo4j:bolt://localhost:7687", "neo4j", "password");
      return apply(jdbi);
   }

   public CompletionStage<Done> trackConsumption(DataAssetNode dataAsset, ApplicationNode application) {
      var query = Templates
         .renderTemplateFromResources(
            "cyphers/track-consumption-app.txt",
            Map.of("dataAsset", dataAsset, "application", application))
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
            Map.of("dataAsset", dataAsset, "model", model))
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
               "dataAsset", dataAsset,
               "user", user,
               "project", project))
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
            Map.of("dataAsset", dataAsset, "application", application))
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
            Map.of("dataAsset", dataAsset, "user", user))
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
               "dataAsset", dataAsset,
               "project", project,
               "user", user))
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
               "model", model,
               "application", application))
         .replace(":", "\\\\:");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

}
