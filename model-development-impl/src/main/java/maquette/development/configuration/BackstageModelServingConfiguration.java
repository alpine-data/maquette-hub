package maquette.development.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maquette.core.config.annotations.ConfigurationProperties;
import maquette.core.config.annotations.Value;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class BackstageModelServingConfiguration {

    /**
     * Base URL for backstage server.
     */
    @Value("url")
    private String url;

    /**
     * The name of the backstage component template.
     */
    @Value("component-template")
    private String componentTemplate;

    /**
     * Environment parameter is sent to Backstage to indicate which Mars/ Maquette environment.
     * should be used to register models.
     */
    @Value("environment")
    private String environment;

    /**
     * A template for the git repository URL. Available variables in the template are
     * `serviceName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("git-repository-url-template")
    private String gitRepositoryUrlTemplate;

    /**
     * A template for the deployment status URL template. Available variables in the template are
     * `serviceName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("deployment-status-url-template")
    private String deploymentStatusUrlTemplate;

    /**
     * A template for the service catalog item URL. Available variables in the template are
     * `serviceName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("service-catalog-url-template")
    private String serviceCatalogUrlTemplate;

    /**
     * A template for the DevOps build pipeline URL. Available variables in the template are
     * `serviceName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("build-pipeline-url-template")
    private String buildPipelineUrlTemplate;

}
