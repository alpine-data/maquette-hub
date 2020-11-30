package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DashboardView implements CommandResult {

   List<ProjectProperties> projects;

}
