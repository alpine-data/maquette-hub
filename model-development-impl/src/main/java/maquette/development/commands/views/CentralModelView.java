package maquette.development.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.server.commands.CommandResult;
import maquette.development.values.WorkSpaceMlflowView;
import maquette.development.values.model.Model;

@Value
@AllArgsConstructor(staticName = "apply")
public class CentralModelView implements CommandResult {

    Model model;

    WorkSpaceMlflowView workspace;
}
