package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.datasets.model.DatasetDetails;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetView implements CommandResult {

   ProjectDetails project;

   DatasetDetails dataset;

   List<CommittedRevision> versions;

   boolean isProjectMember;

   boolean isOwner;

}
