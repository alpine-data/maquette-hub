package maquette.core.services.logs;

import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.logs.LogEntry;
import maquette.core.entities.logs.LogEntryProperties;
import maquette.core.entities.logs.Logs;
import maquette.core.entities.projects.ApplicationEntity;
import maquette.core.entities.projects.ProjectEntity;
import maquette.core.entities.users.UserEntity;
import maquette.core.values.UID;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public class LogsCompanion {

   private final Logs logs;

   private final RuntimeConfiguration runtime;

   public CompletionStage<List<LogEntry>> getLogsByResourcePrefix(UID resource) {
      return logs
         .getByResourcePrefix(resource)
         .thenApply(entries -> entries
            .stream()
            .map(this::enrichLogEntry)
            .collect(Collectors.toList()))
         .thenCompose(Operators::allOf);
   }

   private CompletionStage<LogEntry> enrichLogEntry(LogEntryProperties entry) {
      var userProfileCS = runtime
         .getUsers()
         .findUserById(entry.getUserId())
         .thenCompose(UserEntity::getProfile);

      var projectCS = Operators.optCS(entry.getProject().map(project -> runtime
         .getProjects()
         .getProjectById(project)));

      var projectPropertiesCS = projectCS
         .thenApply(opt -> opt.map(ProjectEntity::getProperties))
         .thenCompose(Operators::optCS);

      var applicationCS = projectCS
         .thenApply(opt -> opt.map(ProjectEntity::getApplications))
         .thenCompose(Operators::optCS)
         .thenApply(opt -> opt.flatMap(apps -> entry.getApplication().map(apps::getApplicationById)))
         .thenCompose(Operators::optCS)
         .thenApply(opt -> opt.map(ApplicationEntity::getProperties))
         .thenCompose(Operators::optCS);

      return Operators.compose(userProfileCS, projectPropertiesCS, applicationCS, (user, project, application) -> LogEntry.apply(
         entry.getLogged(), user,
         application.orElse(null), entry.getEnvironment().orElse(null),
         project.orElse(null), entry.getResource().orElse(null), entry.getAction()));
   }

}
