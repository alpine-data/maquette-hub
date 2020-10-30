package maquette.adapters.users;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemUsersRepositoryConfiguration {

   Path directory;

   public static FileSystemUsersRepositoryConfiguration apply(Config config) {
      var path = config.getString("directory");
      return apply(Path.of(path));
   }

}
