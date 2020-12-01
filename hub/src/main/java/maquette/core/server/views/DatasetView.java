package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.projects.model.Project;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetView implements CommandResult {

   Dataset dataset;

   boolean canAccessData;

   boolean isOwner;

   boolean isMember;

}
