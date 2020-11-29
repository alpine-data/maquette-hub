package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.sandboxes.model.stacks.Stack;
import maquette.core.entities.sandboxes.model.stacks.StackProperties;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAsset;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateSandboxView implements CommandResult {

   List<ProjectProperties> projects;

   List<StackProperties> stacks;

}
