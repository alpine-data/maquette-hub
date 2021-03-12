package maquette.core.entities.projects.model.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.data.binary.BinaryObject;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Value
@AllArgsConstructor(staticName = "apply")
public class VersionFromRegistry {

   String version;

   String description;

   Instant created;

   String stage;

   String user;

   String gitCommit;

   String gitUrl;

   Set<String> flavors;

   BinaryObject explainer;

   public Optional<String> getGitCommit() {
      return Optional.ofNullable(gitCommit);
   }

   public Optional<String> getGitUrl() {
      return Optional.ofNullable(gitUrl);
   }

   public Optional<String> gitUrl() {
      return Optional.ofNullable(gitUrl);
   }

   public Optional<BinaryObject> getExplainer() { return Optional.ofNullable(explainer); }

}
