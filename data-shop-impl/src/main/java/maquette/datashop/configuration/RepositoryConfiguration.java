package maquette.datashop.configuration;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class RepositoryConfiguration {

   String type;

   FileSystemRepositoryConfiguration fs;

   public static RepositoryConfiguration apply(String name) {
      var config = ConfigFactory.load().getConfig("maquette.data-shop");
      var repoConfig = config.getConfig(name);
      var type = repoConfig.getString("type");
      var fsConfig = FileSystemRepositoryConfiguration.apply(config.getConfig("common-settings.fs"));

      return apply(type, fsConfig);
   }

}
