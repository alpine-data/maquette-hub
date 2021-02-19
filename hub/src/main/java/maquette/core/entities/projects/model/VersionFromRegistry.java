package maquette.core.entities.projects.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
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

}
