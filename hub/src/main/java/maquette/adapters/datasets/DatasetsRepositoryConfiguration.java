package maquette.adapters.datasets;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.adapters.infrastructure.FileSystemInfrastructureRepositoryConfiguration;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetsRepositoryConfiguration {

   String type;

   FileSystemDatasetsRepositoryConfiguration fs;

   public static DatasetsRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.datasets-repository");
      var type = config.getString("type");
      var fsConfig = FileSystemDatasetsRepositoryConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
