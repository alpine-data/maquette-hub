package maquette.core.entities.projects.ports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.core.entities.projects.model.MlflowConfiguration;
import maquette.core.entities.projects.model.model.ModelFromRegistry;
import maquette.core.entities.projects.model.model.VersionFromRegistry;
import maquette.core.entities.projects.ports.model.MLModel;
import maquette.core.entities.projects.ports.model.ModelVersionsResponse;
import maquette.core.entities.projects.ports.model.RegisteredModelsResponse;
import maquette.core.values.UID;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.mlflow.api.proto.Service;
import org.mlflow.tracking.MlflowClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class MlflowPort {

   private static final Logger LOG = LoggerFactory.getLogger(MlflowPort.class);

   private final MlflowConfiguration mlflowConfiguration;

   private final UID project;

   private final ObjectMapper om;

   private final OkHttpClient client;

   private final MlflowClient mlflowClient;

   public static MlflowPort apply(MlflowConfiguration mlflowConfiguration, UID project, ObjectMapper om) {
      OkHttpClient client = new OkHttpClient.Builder()
         .readTimeout(3, TimeUnit.MINUTES)
         .build();

      return apply(
         mlflowConfiguration, project, om, client,
         new MlflowClient(mlflowConfiguration.getInternalTrackingUrl()));
   }

   public static MlflowPort apply(MlflowConfiguration mlflowConfiguration, UID project) {
      return apply(mlflowConfiguration, project, ObjectMapperFactory.apply().create());
   }

   public List<ModelFromRegistry> getModels() {
      return query("/api/2.0/preview/mlflow/registered-models/list", RegisteredModelsResponse.class)
         .getRegisteredModels()
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

                  var mlModel = Operators.suppressExceptions(() -> ObjectMapperFactory
                     .apply()
                     .createYaml()
                     .readValue(download(downloadPath), MLModel.class));

                  return VersionFromRegistry.apply(
                     version.getVersion(),
                     version.getDescription(),
                     Instant.ofEpochMilli(version.getCreationTimestamp()),
                     version.getCurrentStage(),
                     run.getInfo().getUserId(),
                     commit,
                     gitUrl,
                     mlModel.getFlavors().keySet());
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
         "/api/2.0/preview/mlflow/model-versions/transition-stage?name=%s&version=%s&stage=%s",
         name, version, stage);

      query(url, JsonNode.class);
   }

   private <T> T query(String url, Class<T> responseType) {
      var requestUrl = String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url);
      var request = new Request.Builder()
         .url(requestUrl)
         .get()
         .build();

      try {
         var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

         if (!response.isSuccessful()) {
            var body = response.body();
            var content = body != null ? Operators.suppressExceptions(body::string) : "";
            content = StringUtils.leftPad(content, 3);
            throw new RuntimeException("Received non-successful response from MLflow `" + requestUrl + "`:\n" + content);
         } else {
            var body = response.body();
            var content = body != null ? Operators.suppressExceptions(body::string) : "{}";
            return Operators.suppressExceptions(() -> om.readValue(content, responseType));
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception occurred requesting information from MLflow `" + requestUrl + "`", e);
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
            throw new RuntimeException("Received non-successful response from MLflow:\n" + content);
         } else {
            var body = response.body();
            return body != null ? Operators.suppressExceptions(body::string) : "";
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception occurred requesting information from MLflow", e);
      }
   }

}
