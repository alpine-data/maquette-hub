package maquette.adapters.sandboxes;

import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class SandboxesRepositoryConfiguration {

   String type;

   FileSystemSandboxesRepositoryConfiguration fs;

   public static SandboxesRepositoryConfiguration apply() {
      var config = ConfigFactory.load().getConfig("maquette.adapters.sandboxes-repository");
      var type = config.getString("type");
      var fsConfig = FileSystemSandboxesRepositoryConfiguration.apply(config.getConfig("fs"));
      return apply(type, fsConfig);
   }

}
