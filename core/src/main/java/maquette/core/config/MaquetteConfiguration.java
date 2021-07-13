package maquette.core.config;

import lombok.*;
import maquette.core.config.annotations.ConfigurationProperties;
import maquette.core.config.annotations.Value;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class MaquetteConfiguration {

   @Value("name")
   String name;

   @Value("version")
   String version;

   @Value("environment")
   String environment;

   @Value("core")
   CoreConfiguration core;

   public static MaquetteConfiguration apply() {
      return Configs.mapToConfigClass(MaquetteConfiguration.class, "maquette");
   }

}
