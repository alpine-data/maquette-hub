package maquette.core.server.commands.projects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.results.MessageResult;
import maquette.core.services.ApplicationServices;
import maquette.core.values.user.User;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class UpdateProjectPropertiesCommand implements Command {

   String project;

   String name;

   String title;

   String summary;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      if (Objects.isNull(project) || project.length() == 0) {
         throw new IllegalArgumentException("`project` must be defined");
      }

      return services
         .getProjectServices()
         .update(user, project, name, title, summary)
         .thenApply(done -> MessageResult.apply("Successfully updated project."));
   }

   @Override
   public Command example() {
      return apply("some-project", "other-project-name", "Other Project Name", "Some summary.");
   }

}
