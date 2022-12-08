package maquette.development.ports.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;
import maquette.development.configuration.BackstageModelServingConfiguration;
import maquette.development.configuration.ModelDevelopmentConfiguration;
import maquette.development.values.model.services.ModelServiceProperties;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply")
public final class BackstageModelServing implements ModelServingPort {

    private final BackstageModelServingConfiguration config;

    private final ObjectMapper om;

    private final OkHttpClient client;

    public static BackstageModelServing apply(ObjectMapper om) {
        var config = ModelDevelopmentConfiguration.apply().getModelServing().getBackstage();
        return apply(om, config);
    }

    public static BackstageModelServing apply(ObjectMapper om, BackstageModelServingConfiguration config) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .build();

        return apply(config, om, httpClient);
    }

    @Override
    public CompletionStage<ModelServiceProperties> createModel(
        String modelName,
        String modelVersion,
        String environment,
        String serviceName,
        String mlflowInstanceId,
        String maintainerName,
        String maintainerEmail
    ) {

        var backstageRequest = BackstageRequest.apply(
            this.config.getComponentTemplate(),
            ComponentProperties.apply(
                modelName,
                modelVersion,
                environment,
                serviceName,
                mlflowInstanceId,
                maintainerName,
                maintainerEmail
            ));

        var json = Operators.suppressExceptions(() -> this.om.writeValueAsString(backstageRequest));

        var request = new Request.Builder()
            .url(this.config.getUrl() + "/api/scaffolder/v2/tasks")
            .post(RequestBody.create(json, MediaType.parse("application/json")))
            .build();

        ResponseBody responseBody = null;
        try {
            var response = Operators.suppressExceptions(() -> this.client
                .newCall(request)
                .execute());
            responseBody = response.body();

            if (!response.isSuccessful()) {
                var content = responseBody != null ? Operators.suppressExceptions(responseBody::string) : "";
                content = StringUtils.leftPad(content, 3);
                throw new RuntimeException("Received non-successful response from auto-infra:\n" + content);
            } else {
                var content = responseBody != null ? Operators.suppressExceptions(responseBody::string) : "";
                var backstageTaskId = this.om.readValue(content, BackstageResponse.class).getId();

                var urlsMap = Maps.<String, String>newHashMap();
                urlsMap.put("Deployment Status", this.config.getUrl() + "/foo/bar/" + backstageTaskId);
                urlsMap.put("Service Catalog", this.config.getUrl() + "/catalog/" + serviceName);
                urlsMap.put("Git Repository", "http://i-dont-know-yet.com");
                urlsMap.put("Build Pipeline", "http://i-dont-know-yet.com");

                return CompletableFuture.completedFuture(
                    ModelServiceProperties.apply(
                        serviceName,
                        urlsMap
                    )
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred sending request to Backstage.", e);
        } finally {
            if (responseBody != null) responseBody.close();
        }
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    private static class ComponentProperties {
        String modelName;
        String modelVersion;
        String environment;

        String serviceName;
        String mlflowInstanceId;
        String maintainerName;
        String maintainerEmail;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    private static class BackstageRequest {

        String templateRef;

        ComponentProperties values;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    private static class BackstageResponse {

        String id;

    }

}
