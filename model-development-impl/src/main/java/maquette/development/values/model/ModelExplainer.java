package maquette.development.values.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.nio.file.Path;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ModelExplainer {

   Path file;

   String externalUrl;

   public static ModelExplainer apply(Path file) {
      return apply(file, null);
   }

   public Optional<String> getExternalUrl() {
      return Optional.ofNullable(externalUrl);
   }

}
