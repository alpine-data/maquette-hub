package maquette.development.ports.mlprojects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;
import maquette.core.common.Templates;
import maquette.development.configuration.BackstageMLProjectCreationConfiguration;
import maquette.development.configuration.ModelDevelopmentConfiguration;
import maquette.development.values.mlproject.MLProjectType;
import maquette.development.values.mlproject.MachineLearningProject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply")
public class BackstageMLProjectCreationPort implements MLProjectCreationPort {

    private final BackstageMLProjectCreationConfiguration config;

    private final ObjectMapper om;

    private final OkHttpClient client;

    public static BackstageMLProjectCreationPort apply(ObjectMapper om) {
        var config = ModelDevelopmentConfiguration.apply().getMlProjectsConfiguration().getBackstage();
        return apply(om, config);
    }

    public static BackstageMLProjectCreationPort apply(
        ObjectMapper om,
        BackstageMLProjectCreationConfiguration config
    ) {

        OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .build();

        return apply(config, om, httpClient);
    }

    @Override
    public CompletionStage<MachineLearningProject> createMachineLearningProject(String workspaceName,
                                                                                String projectName,
                                                                                MLProjectType templateType) {
        var backstageRequest = BackstageRequest.apply(
            this.config.getComponentTemplate(),
            ComponentProperties.apply(
                projectName,
                workspaceName
            ));

        var json = Operators.suppressExceptions(() -> this.om.writeValueAsString(backstageRequest));

        var request = new Request.Builder()
            .url(this.config.getUrl() + "/api/scaffolder/v2/tasks")
            .post(RequestBody.create(json, MediaType.parse("application/json")))
            .build();

        try (
            var response = Operators
                .suppressExceptions(() -> this.client
                    .newCall(request)
                    .execute()
                );

            var responseBody = response.body()
        ) {
            var content = responseBody != null ? Operators.suppressExceptions(responseBody::string) : "";

            if (!response.isSuccessful()) {
                content = StringUtils.leftPad(content, 3);
                throw new RuntimeException("Received non-successful response from auto-infra:\n" + content);
            } else {
                var contentFinal = content;

                var backstageTaskId = Operators
                    .suppressExceptions(
                        () -> this.om.readValue(contentFinal,
                            BackstageMLProjectCreationPort.BackstageResponse.class)
                    )
                    .getId();

                var templateParameters = Maps.<String, Object>newHashMap();
                templateParameters.put("projectName", projectName);
                templateParameters.put("backstageTaskId", backstageTaskId);
                templateParameters.put("backstageUrl", this.config.getUrl());

                return CompletableFuture.completedFuture(
                    MachineLearningProject.apply(
                        projectName,
                        Templates.renderTemplateFromString(config.getGitRepositoryUrlTemplate(),
                            templateParameters),
                        Templates.renderTemplateFromString(config.getGitUrlTemplate(), templateParameters),
                        Templates.renderTemplateFromString(config.getServiceCatalogUrlTemplate(),
                            templateParameters)
                    )
                );
            }
        }
    }

    /**
     * This class must match required properties from Backstage Template.
     * <a href="https://dev.azure.com/zurichinsurance/Space/_git/template-ml-python">ML Template Project</a>.
     */
    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    private static class ComponentProperties {

        private static final String ATTR_NAME = "name";

        private static final String ATTR_WORKSPACE_NAME = "mars_workspace_name";

        @JsonProperty(ATTR_NAME)
        String projectName;

        @JsonProperty(ATTR_WORKSPACE_NAME)
        String workspaceName;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    private static class BackstageRequest {

        String templateRef;

        BackstageMLProjectCreationPort.ComponentProperties values;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    private static class BackstageResponse {

        String id;

    }

}
