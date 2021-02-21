package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.ProjectView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ProjectViewCommand implements Command {

   String project;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      var projectCS = services
         .getProjectServices()
         .get(user, project);

      var modelsCS = services
         .getProjectServices()
         .getModels(user, project)
         .thenApply(models -> models
            .stream()
            .map(m -> services
               .getProjectServices()
               .getModel(user, project, m.getName())))
         .thenCompose(Operators::allOf);

      var usersCS = services
         .getUserServices()
         .getUsers(user)
         .thenApply(list -> list
            .stream()
            .collect(Collectors.toMap(UserProfile::getId, u -> u)));

      return Operators.compose(projectCS, modelsCS, usersCS, (project, models, users) -> {
         var isMember = project.isMember(user);
         var isAdmin = project.isMember(user, ProjectMemberRole.ADMIN);

         return ProjectView.apply(project, models, users, isMember, isAdmin);
      });
   }

   @Override
   public Command example() {
      return apply("some-project");
   }

}
