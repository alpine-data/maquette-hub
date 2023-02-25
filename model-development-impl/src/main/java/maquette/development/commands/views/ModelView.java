package maquette.development.commands.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.server.commands.CommandResult;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelPermissions;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelView implements CommandResult {

    Model model;

}
