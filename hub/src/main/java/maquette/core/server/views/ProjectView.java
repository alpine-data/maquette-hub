package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.StackProperties;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectView implements CommandResult {

   Project project;

   boolean isMember;

   boolean isAdmin;

}
