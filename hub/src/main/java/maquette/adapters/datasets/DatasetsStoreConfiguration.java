package maquette.adapters.datasets;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetsStoreConfiguration {

   String type;

   FileSystemDatasetsStoreConfiguration fs;

   public static DatasetsStoreConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.datasets-store");
      var type = config.getString("type");
      var fsConfig = FileSystemDatasetsStoreConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
