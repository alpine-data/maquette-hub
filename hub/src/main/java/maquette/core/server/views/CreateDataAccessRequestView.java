package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.server.CommandResult;
import maquette.core.values.data.DataAssetProperties;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateDataAccessRequestView implements CommandResult {

   ProjectDetails project;

   DataAssetProperties asset;

   List<ProjectProperties> projects;

   boolean isOwner;

   boolean isSubscribe;

}
