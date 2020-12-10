package maquette.adapters.datasources;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemDataSourcesRepositoryConfiguration {

   Path directory;

   public static FileSystemDataSourcesRepositoryConfiguration apply(Config config) {
      var path = config.getString("directory");
      return apply(Path.of(path));
   }

}
