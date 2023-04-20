package maquette.development.entities.mlflow.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import maquette.core.common.Operators;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.binary.BinaryObjects;
import maquette.development.entities.mlflow.MlflowConfiguration;
import maquette.development.entities.mlflow.apimodel.RegisteredModelsResponse;
import maquette.development.entities.mlflow.explainer.ExplainerArtifact;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.mlflow.api.proto.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class MlflowClient {

    private static final int RETRIES = 3;

    private static final int INITIAL_RETRY_TIMEOUT_SECONDS = 1;

    private final MlflowConfiguration mlflowConfiguration;

    private final ObjectMapper om;

    private final OkHttpClient client;

    private final org.mlflow.tracking.MlflowClient mlflowClient;

    public static MlflowClient apply(MlflowConfiguration mlflowConfiguration, ObjectMapper om) {
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .build();

        return apply(
            mlflowConfiguration, om, client,
            new org.mlflow.tracking.MlflowClient(mlflowConfiguration.getInternalTrackingUrl()));
    }

    public static MlflowClient apply(MlflowConfiguration mlflowConfiguration) {
        return apply(
            mlflowConfiguration,
            DefaultObjectMapperFactory.apply().createJsonMapper()
        );
    }

    public List<ModelFromRegistry> getModels() {
        return Optional
            .ofNullable(query("/api/2.0/preview/mlflow/registered-models/list",
                RegisteredModelsResponse.class).getRegisteredModels())
            .orElseGet(() -> {
                System.out.println("Received empty list of models from " + this.mlflowConfiguration.getInternalTrackingUrl());
                return List.of();
            })
            .stream()
            .map(registeredModel -> ModelFromRegistry.apply(this, mlflowClient, mlflowConfiguration, registeredModel))
            .collect(Collectors.toList());
    }

    public ModelFromRegistry getModel(String name) {
        return findModel(name).orElseThrow();
    }

    public Optional<ModelFromRegistry> findModel(String name) {
        return Optional
            .ofNullable(query("/api/2.0/preview/mlflow/registered-models/list",
                RegisteredModelsResponse.class).getRegisteredModels())
            .orElseGet(() -> {
                System.out.println("Received empty list of models from " + this.mlflowConfiguration.getInternalTrackingUrl());
                return List.of();
            })
            .stream()
            .filter(registeredModel -> registeredModel.getName().equalsIgnoreCase(name))
            .map(registeredModel -> ModelFromRegistry.apply(this, mlflowClient, mlflowConfiguration, registeredModel))
            .findFirst();
    }

    public void transitionStage(String name, String version, String stage) {
        var url = String.format(
            "%s/api/2.0/preview/mlflow/model-versions/transition-stage",
            mlflowConfiguration.getInternalTrackingUrl());

        var req = TransitionStageRequest.apply(name, version, stage, false);
        var json = Operators.suppressExceptions(() -> om.writeValueAsString(req));

        var request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(json, MediaType.parse("application/json")))
            .build();

        query(request, JsonNode.class);
    }

    <T> T query(String url, Class<T> responseType) {
        var requestUrl = String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url);
        var request = new Request.Builder()
            .url(requestUrl)
            .get()
            .build();

        return query(request, responseType);
    }

    <T> T query(Request request, Class<T> responseType) {
        return Operators.retryWithBackOffTimeout(() -> {
            try (var response = Operators.suppressExceptions(() -> client
                .newCall(request)
                .execute())) {

                if (!response.isSuccessful()) {
                    try (var body = response.body()) {
                        var content = body != null ? Operators.suppressExceptions(body::string) : "";
                        content = StringUtils.leftPad(content, 3);
                        throw new RuntimeException(
                            "Received non-successful response from MLflow `" + request.url() + "`:\n" + content);
                    }
                } else {
                    try (var body = response.body()) {
                        var content = body != null ? Operators.suppressExceptions(body::string) : "{}";
                        return Operators.suppressExceptions(() -> om.readValue(content, responseType));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred requesting information from MLflow `" + request.url() + "`",
                    e);
            }
        }, RETRIES, INITIAL_RETRY_TIMEOUT_SECONDS);
    }

    /**
     * downloads artifacts
     * @return artifact as string
     */
    public String downloadArtifact(String artifactPath, String runId){

        var downloadUrl = String.format(
                "%s/get-artifact?path=%s&run_uuid=%s",
                mlflowConfiguration.getMlflowBasePath(),
                artifactPath,
                runId);

        return this.download(downloadUrl);

    }

    public String download(String url) {
        var request = new Request.Builder()
            .url(String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url))
            .get()
            .build();

        return Operators.retryWithBackOffTimeout(() -> {
            try (var response = Operators.suppressExceptions(() -> client
                .newCall(request)
                .execute())) {

                if (!response.isSuccessful()) {
                    try (var body = response.body()) {
                        var content = body != null ? Operators.suppressExceptions(body::string) : "";
                        content = StringUtils.leftPad(content, 3);
                        throw new RuntimeException("Received non-successful response from MLflow:\n" + content);
                    }
                } else {
                    try (var body = response.body()) {
                        return body != null ? Operators.suppressExceptions(body::string) : "";
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred requesting information from MLflow", e);
            }
        }, RETRIES, INITIAL_RETRY_TIMEOUT_SECONDS);
    }

    Optional<BinaryObject> downloadFile(String url) {
        var request = new Request.Builder()
            .url(String.format("%s%s", mlflowConfiguration.getInternalTrackingUrl(), url))
            .get()
            .build();

        return Operators.retryWithBackOffTimeout(() -> {
            try (var response = Operators.suppressExceptions(() -> client
                .newCall(request)
                .execute())) {

                if (!response.isSuccessful()) {
                    return Optional.empty();
                } else {
                    try (var body = response.body()) {
                        if (body != null) {
                            return Optional.ofNullable(BinaryObjects.fromInputStream(body.byteStream()));
                        } else {
                            return Optional.empty();
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred requesting information from MLflow", e);
            }
        }, RETRIES, INITIAL_RETRY_TIMEOUT_SECONDS);
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
