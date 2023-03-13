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
public class BackstageMLProjectCreationConfiguration {

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
     * A template for the git repository URL. Available variables in the template are
     * `projectName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("git-repository-url-template")
    private String gitRepositoryUrlTemplate;

    /**
     * A template for the git repository URL. Available variables in the template are
     * `projectName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("git-repository-url-template")
    private String gitUrlTemplate;

    /**
     * A template for the service catalog item URL. Available variables in the template are
     * `projectName`, `backstageTaskId` and `backstageUrl`.
     */
    @Value("service-catalog-url-template")
    private String serviceCatalogUrlTemplate;

}
