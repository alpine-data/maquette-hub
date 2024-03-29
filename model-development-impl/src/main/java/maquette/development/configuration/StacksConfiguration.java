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
public class StacksConfiguration {

    @Value("python")
    PythonStackConfiguration python;

    @Value("python-gpu")
    PythonGPUStackConfiguration pythonGpu;

    public static StacksConfiguration apply() {
        return Configs.mapToConfigClass(StacksConfiguration.class, "maquette.model-development.stacks");
    }

}
