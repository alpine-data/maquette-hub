package maquette.development.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maquette.core.config.Configs;
import maquette.core.config.annotations.ConfigurationProperties;
import maquette.core.config.annotations.Value;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class ModelDevelopmentConfiguration {

    @Value("stacks")
    private StacksConfiguration stacks;

    @Value("model-serving")
    private ModelServingConfiguration modelServing;

    @Value("ml-projects")
    private MLProjectsConfiguration mlProjectsConfiguration;

    public static ModelDevelopmentConfiguration apply() {
        return Configs.mapToConfigClass(ModelDevelopmentConfiguration.class, "maquette.model-development");
    }

}
