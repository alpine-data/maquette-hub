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
public class MlflowConfiguration {

    /**
     * Whether automatic updates of Mlflow model information are enabled or not.
     */
    @Value("sync-enabled")
    private boolean isSyncEnabled;

    /**
     * A cron expression to specify how often the update should be executed.
     */
    @Value("sync-cron")
    private String syncCron;

}
