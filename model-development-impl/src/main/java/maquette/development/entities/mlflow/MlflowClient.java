package maquette.development.entities.mlflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import maquette.core.common.Operators;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.values.UID;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.binary.BinaryObjects;
import maquette.development.entities.mlflow.model.MLModel;
import maquette.development.entities.mlflow.model.ModelVersionsResponse;
import maquette.development.entities.mlflow.model.RegisteredModelsResponse;
import maquette.development.values.MlflowConfiguration;
import maquette.development.values.model.mlflow.ModelFromRegistry;
import maquette.development.values.model.mlflow.VersionFromRegistry;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.mlflow.api.proto.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class MlflowClient {

   private static final Logger LOG = LoggerFactory.getLogger(MlflowClient.class);

   private final MlflowConfiguration mlflowConfiguration;

   private final UID project;

   private final ObjectMapper om;

   private final OkHttpClient client;

   private final org.mlflow.tracking.MlflowClient mlflowClient;

   public static MlflowClient apply(MlflowConfiguration mlflowConfiguration, UID project, ObjectMapper om) {
      OkHttpClient client = new OkHttpClient.Builder()
         .readTimeout(3, TimeUnit.MINUTES)
         .build();

      return apply(
         mlflowConfiguration, project, om, client,
         new org.mlflow.tracking.MlflowClient(mlflowConfiguration.getInternalTrackingUrl()));
   }

   public static MlflowClient apply(MlflowConfiguration mlflowConfiguration, UID project) {
      return apply(mlflowConfiguration, project, DefaultObjectMapperFactory.apply().createJsonMapper());
   }

   public List<ModelFromRegistry> getModels() {
      return Optional
         .ofNullable(query("/api/2.0/preview/mlflow/registered-models/list", RegisteredModelsResponse.class).getRegisteredModels())
         .orElse(List.of())
         .stream()
         .map(model -> {
            var versions = query(
               String.format("/api/2.0/preview/mlflow/model-versions/search?filter=name%%3D%%27%s%%27", model.getName()),
               ModelVersionsResponse.class)
               .getModelVersions()
               .stream()
               .map(version -> {
                  var run = mlflowClient.getRun(version.getRunId());

                  var commit = run
                     .getData()
                     .getTagsList()
                     .stream()
                     .filter(tag -> tag.getKey().equals("mlflow.source.git.commit"))
                     .map(Service.RunTag::getValue)
                     .findFirst()
                     .orElse(null);

                  var gitUrl = run
                     .getData()
                     .getTagsList()
                     .stream()
                     .filter(tag -> tag.getKey().equals("mlflow.source.git.repoURL"))
                     .map(Service.RunTag::getValue)
                     .findFirst()
                     .orElse(null);


                  var modelPath = version
                     .getSource()
                     .substring(version.getSource().indexOf("artifacts") + "artifacts/".length());

                  var downloadPath = String.format(
                     "%s/get-artifact?path=%s%%2FMLmodel&run_uuid=%s",
                     mlflowConfiguration.getMlflowBasePath(project),
                     modelPath.replace("/", "%2F"),
                     version.getRunId());

                  var mlModel = Operators.suppressExceptions(() -> DefaultObjectMapperFactory
                     .apply()
                     .createYamlMapper()
                     .readValue(download(downloadPath), MLModel.class));

                  var explainerPath = String.format(
                     "%s/get-artifact?path=xpl.pkl&run_uuid=%s",
                     mlflowConfiguration.getMlflowBasePath(project),
                     version.getRunId());

                  var explainer = downloadFile(explainerPath).orElse(null);

                  return VersionFromRegistry.apply(
                     version.getVersion(),
                     version.getDescription(),
                     Instant.ofEpochMilli(version.getCreationTimestamp()),
                     version.getCurrentStage(),
                     run.getInfo().getUserId(),
                     commit,
                     gitUrl,
                     mlModel.getFlavors().keySet(),
                     explainer);
               })
               .sorted(Comparator.comparing(VersionFromRegistry::getCreated).reversed())
               .collect(Collectors.toList());

            return ModelFromRegistry.apply(
               model.getName(),
               Instant.ofEpochMilli(model.getCreationTimestamp()),
               Instant.ofEpochMilli(model.getLastUpdatedTimestamp()),
               versions);
         })
         .sorted(Comparator.comparing(ModelFromRegistry::getName))
         .collect(Collectors.toList());
   }

   public ModelFromRegistry getModel(String name) {
      return findModel(name).orElseThrow();
   }

   public Optional<ModelFromRegistry> findModel(String name) {
      return getModels()
         .stream()
         .filter(m -> m.getName().equals(name))
         .findFirst();
   }

   public void transitionStage(String name, String version, String stage) {
      var url = String.format(
         "%s/api/2.0/preview/mlflow/model-versions/transition-stage",
         mlflowConfiguration.getInternalTrackingUrl(), name, version, stage);

      var req = TransitionStageRequest.apply(name, version, stage, false);
      var json = Operators.suppressExceptions(() -> om.writeValueAsString(req));

      var request = new Request.Builder()
         .url(url)
         .post(RequestBody.create(json, MediaType.parse("application/json")))
         .build();

      query(request, JsonNode.class);
   }

   private <T> T query(String url, Class<T> responseType) {
      var requestUrl = String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url);
      var request = new Request.Builder()
         .url(requestUrl)
         .get()
         .build();

      return query(request, responseType);
   }

   private <T> T query(Request request, Class<T> responseType) {
      try {
         var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

         if (!response.isSuccessful()) {
            var body = response.body();
            var content = body != null ? Operators.suppressExceptions(body::string) : "";
            content = StringUtils.leftPad(content, 3);
            if (body != null) body.close();
            throw new RuntimeException("Received non-successful response from MLflow `" + request.url() + "`:\n" + content);
         } else {
            var body = response.body();
            var content = body != null ? Operators.suppressExceptions(body::string) : "{}";
            var result = Operators.suppressExceptions(() -> om.readValue(content, responseType));
            if (body != null) body.close();
            return result;
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception occurred requesting information from MLflow `" + request.url() + "`", e);
      }
   }

   private String download(String url) {
      var request = new Request.Builder()
         .url(String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url))
         .get()
         .build();

      try {
         var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

         if (!response.isSuccessful()) {
            var body = response.body();
            var content = body != null ? Operators.suppressExceptions(body::string) : "";
            content = StringUtils.leftPad(content, 3);
            if (body != null) body.close();
            throw new RuntimeException("Received non-successful response from MLflow:\n" + content);
         } else {
            var body = response.body();
            var result = body != null ? Operators.suppressExceptions(body::string) : "";
            if (body != null) body.close();
            return result;
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception occurred requesting information from MLflow", e);
      }
   }

   private Optional<BinaryObject> downloadFile(String url) {
      var request = new Request.Builder()
         .url(String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url))
         .get()
         .build();

      try {
         var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

         if (!response.isSuccessful()) {
            return Optional.empty();
         } else {
            var body = response.body();

            if (body != null) {
               var result = Optional.ofNullable(BinaryObjects.fromInputStream(body.byteStream()));
               body.close();
               return result;
            } else {
               return Optional.empty();
            }
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception occurred requesting information from MLflow", e);
      }
   }

   @Data
   @AllArgsConstructor(staticName = "apply")
   private static class TransitionStageRequest {

      String name;

      String version;

      String stage;

      @JsonProperty("archive_existing_versions")
      boolean archiveExistingVersions;

   }

}
