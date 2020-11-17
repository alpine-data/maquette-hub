package maquette.adapters.sandboxes;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemSandboxesRepositoryConfiguration {

   Path directory;

   public static FileSystemSandboxesRepositoryConfiguration apply(Config config) {
      var path = config.getString("directory");
      return apply(Path.of(path));
   }

}
