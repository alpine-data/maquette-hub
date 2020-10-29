package maquette.adapters.projects;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemProjectsRepositoryConfiguration {

   Path directory;

   public static FileSystemProjectsRepositoryConfiguration apply(Config config) {
      var path = config.getString("directory");
      return apply(Path.of(path));
   }

}