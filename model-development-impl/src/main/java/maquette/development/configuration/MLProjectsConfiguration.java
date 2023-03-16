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
public class MLProjectsConfiguration {

    /**
     * Specifies a regex to which new ML Project names must comply.
     */
    @Value("ml-project-name-regex")
    String mlProjectNameRegex;

    /**
     * Configuration for backstage endpoint if used.
     */
    @Value("backstage")
    BackstageMLProjectCreationConfiguration backstage;

}
