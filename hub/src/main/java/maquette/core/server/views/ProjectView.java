package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.entities.sandboxes.model.stacks.StackProperties;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectView implements CommandResult {

   ProjectDetails project;

   List<DataAssetProperties> assets;

   List<SandboxProperties> sandboxes;

   List<StackProperties> stacks;

   boolean isMember;

}
