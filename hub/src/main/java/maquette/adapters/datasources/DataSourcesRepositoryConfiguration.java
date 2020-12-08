package maquette.adapters.datasources;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourcesRepositoryConfiguration {

   String type;

   FileSystemDataSourcesRepositoryConfiguration fs;

   public static DataSourcesRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.data-sources-repository");
      var type = config.getString("type");
      var fsConfig = FileSystemDataSourcesRepositoryConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
