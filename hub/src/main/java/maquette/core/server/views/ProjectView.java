package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.model.Model;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.CommandResult;

import java.util.List;
import java.util.Map;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectView implements CommandResult {

   Project project;

   List<Model> models;

   Map<String, UserProfile> users;

   boolean isMember;

   boolean isAdmin;

}
