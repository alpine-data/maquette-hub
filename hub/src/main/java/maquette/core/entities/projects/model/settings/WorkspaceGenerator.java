package maquette.core.entities.projects.model.settings;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class WorkspaceGenerator {

   String name;

   String repository;

   String description;

}
