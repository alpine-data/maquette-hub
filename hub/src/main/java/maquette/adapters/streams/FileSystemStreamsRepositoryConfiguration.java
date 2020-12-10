package maquette.adapters.streams;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemStreamsRepositoryConfiguration {

   Path directory;

   public static FileSystemStreamsRepositoryConfiguration apply(Config config) {
      var path = config.getString("directory");
      return apply(Path.of(path));
   }

}
