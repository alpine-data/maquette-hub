package maquette.core.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maquette.common.config.annotations.ConfigurationProperties;
import maquette.common.config.annotations.Value;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public final class GitClientSettings {

   @Value("username")
   private String username;

   @Value("token")
   private String token;

}