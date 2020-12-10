package maquette.adapters.collections;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class CollectionsRepositoryConfiguration {

   String type;

   FileSystemCollectionsRepositoryConfiguration fs;

   public static CollectionsRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.collections-repository");
      var type = config.getString("type");
      var fsConfig = FileSystemCollectionsRepositoryConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
