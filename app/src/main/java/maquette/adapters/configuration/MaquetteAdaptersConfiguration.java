package maquette.adapters.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maquette.core.config.Configs;
import maquette.core.config.annotations.ConfigurationProperties;
import maquette.core.config.annotations.Value;

import java.util.List;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class MaquetteAdaptersConfiguration {

    /**
     * Environment names which trigger the model to be promoted into Staging in MLflow.
     *
     * The environment name is reported to Maquette when calling
     * {@link maquette.operations.commands.RegisterDeployedModelServiceInstanceCommand}.
     */
    @Value("promote-model-to-staging-for-environments")
    List<String> promoteModelToStagingForEnvironments;

    /**
     * Environment names which trigger the model to be promoted into Production in MLflow.
     *
     * The environment name is reported to Maquette when calling
     * {@link maquette.operations.commands.RegisterDeployedModelServiceInstanceCommand}.
     */
    @Value("promote-model-to-production-for-environment")
    List<String> promoteModelToProductionForEnvironments;

    public static MaquetteAdaptersConfiguration apply() {
        return Configs.mapToConfigClass(MaquetteAdaptersConfiguration.class, "maquette.adapters");
    }

}
