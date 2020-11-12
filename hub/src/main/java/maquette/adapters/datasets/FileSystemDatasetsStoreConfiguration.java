package maquette.adapters.datasets;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemDatasetsStoreConfiguration {

   Path directory;

   public static FileSystemDatasetsStoreConfiguration apply(Config config) {
      var path = config.getString("directory");
      return apply(Path.of(path));
   }

}
