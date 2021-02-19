package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.users.model.UserProfile;

import java.time.Instant;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelVersion {

   String version;

   Instant registeredAt;

   UserProfile registeredBy;

   String stage;

}
