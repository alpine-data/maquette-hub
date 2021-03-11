package maquette.core.entities.projects.model.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.exceptions.ModelVersionNotFoundException;
import maquette.core.entities.projects.model.model.governance.CheckException;
import maquette.core.entities.projects.model.model.governance.CheckWarning;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.GrantedAuthorization;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Model {

   String title;

   String name;

   Set<String> flavours;

   String description;

   List<String> warnings;

   List<ModelVersion> versions;

   ActionMetadata created;

   ActionMetadata updated;

   List<GrantedAuthorization<ModelMemberRole>> members;

   ModelPermissions permissions;

   public static Model fromProperties(ModelProperties properties, List<GrantedAuthorization<ModelMemberRole>> members, ModelPermissions permissions) {

      return apply(
         properties.getTitle(),
         properties.getName(),
         properties.getFlavours(),
         properties.getDescription(),
         properties.getWarnings(),
         properties.getVersions(),
         properties.getCreated(),
         properties.getUpdated(),
         members,
         permissions);
   }

   public ModelVersion getVersion(String version) {
      return findVersion(version).orElseThrow(() -> ModelVersionNotFoundException.apply(name, version));
   }

   public Optional<ModelVersion> findVersion(String version) {
      return versions
         .stream()
         .filter(v -> v.getVersion().equals(version))
         .findAny();
   }

   @JsonProperty("exceptions")
   public long getExceptions() {
      return versions
         .stream()
         .max(Comparator.comparing(m -> m.getRegistered().getAt()))
         .map(version -> {
            var count = version.getCodeQualityChecks().stream().filter(r -> r instanceof CheckException).count();
            count += version.getDataDependencyChecks().stream().filter(r -> r instanceof CheckException).count();

            return count;
         })
         .orElse(0L);
   }

   @JsonProperty("warnings")
   public long getWarnings() {
      return versions
         .stream()
         .max(Comparator.comparing(m -> m.getRegistered().getAt()))
         .map(version -> {
            var count = version.getCodeQualityChecks().stream().filter(r -> r instanceof CheckWarning).count();
            count += version.getDataDependencyChecks().stream().filter(r -> r instanceof CheckWarning).count();

            return count;
         })
         .orElse(0L);
   }

   public Model withVersion(ModelVersion version) {
      var filtered = this
         .versions
         .stream()
         .filter(v -> !v.getVersion().equals(version.getVersion()));

      var versions = Stream
         .concat(filtered, Stream.of(version))
         .collect(Collectors.toList());

      return withVersions(versions);
   }

}
