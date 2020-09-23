package maquette.adapters.infrastructure;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class InfrastructureRepositoryConfiguration {

   String type;

   FileSystemInfrastructureRepositoryConfiguration fs;

   public static InfrastructureRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.infrastructure-repository");
      var type = config.getString("type");
      var fsConfig = FileSystemInfrastructureRepositoryConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
