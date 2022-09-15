package maquette.datashop.configuration;

import com.typesafe.config.Config;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileSystemRepositoryConfiguration {

    Path directory;

    public static FileSystemRepositoryConfiguration apply(Config config) {
        var path = config.getString("directory");
        return apply(Path.of(path));
    }

}
