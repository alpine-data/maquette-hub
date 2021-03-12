package maquette.core.entities.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.nio.file.Path;

@Value
@AllArgsConstructor(staticName = "apply")
public class Volume {

   Path source;

   String path;

   public String toVolumeExpr() {
      return String.format("%s:%s", source, path);
   }

}
