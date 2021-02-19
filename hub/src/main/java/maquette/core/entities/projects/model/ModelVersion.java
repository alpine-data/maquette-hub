package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.values.ActionMetadata;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelVersion {

   String version;

   String description;

   ActionMetadata registered;

   ActionMetadata updated;

   String stage;

}
