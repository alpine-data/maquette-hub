package maquette.core.entities.projects.model.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.exceptions.ModelVersionNotFoundException;
import maquette.core.values.ActionMetadata;
import maquette.core.values.authorization.GrantedAuthorization;

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

   public static Model fromProperties(ModelProperties properties, List<GrantedAuthorization<ModelMemberRole>> members) {
      return apply(
         properties.getTitle(),
         properties.getName(),
         properties.getFlavours(),
         properties.getDescription(),
         properties.getWarnings(),
         properties.getVersions(),
         properties.getCreated(),
         properties.getUpdated(),
         members);
   }

   public ModelVersion getVersion(String version){
      return findVersion(version).orElseThrow(() -> ModelVersionNotFoundException.apply(name, version));
   }

   public Optional<ModelVersion> findVersion(String version) {
      return versions
         .stream()
         .filter(v -> v.getVersion().equals(version))
         .findAny();
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
