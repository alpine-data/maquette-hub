package maquette.development.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.server.commands.CommandResult;
import maquette.development.values.Workspace;
import maquette.development.values.stacks.StackProperties;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class WorkspaceView implements CommandResult {

    Workspace workspace;

    List<StackProperties> stacks;

}
