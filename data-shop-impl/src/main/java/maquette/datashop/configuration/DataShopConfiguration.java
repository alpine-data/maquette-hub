package maquette.datashop.configuration;

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
public class DataShopConfiguration {

    @Value("default-data-owner")
    private String defaultDataOwner;

    @Value("collections")
    private CollectionsConfiguration collections;

    public static DataShopConfiguration apply() {
        return Configs.mapToConfigClass(DataShopConfiguration.class, "maquette.data-shop");
    }

}
