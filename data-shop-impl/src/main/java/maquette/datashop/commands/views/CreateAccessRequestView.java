package maquette.datashop.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.server.commands.CommandResult;
import maquette.datashop.ports.Workspace;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public final class CreateAccessRequestView implements CommandResult {

    List<Workspace> workspaces;

}
