package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.sandboxes.Sandbox;
import maquette.core.entities.projects.model.sandboxes.stacks.StackProperties;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class SandboxView implements CommandResult {

   Project project;

   Sandbox sandbox;

   List<StackProperties> stacks;

}
