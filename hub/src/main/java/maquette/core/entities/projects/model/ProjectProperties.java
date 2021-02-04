package maquette.core.entities.projects.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectProperties {

   UID id;

   String name;

   String title;

   String summary;

   ActionMetadata created;

   ActionMetadata modified;

   MlflowConfiguration mlflowConfiguration;

   public static ProjectProperties apply(
      UID id, String name, String title, String summary, ActionMetadata created, ActionMetadata modified) {

      return apply(id, name, title, summary, created, modified, null);
   }

   public Optional<MlflowConfiguration> getMlflowConfiguration() {
      return Optional.ofNullable(mlflowConfiguration);
   }

}
