package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class MountedVolume {

   Path source;

   String path;

}
