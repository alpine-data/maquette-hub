package maquette.core.entities.logs;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.UID;
import maquette.core.values.user.ApplicationContext;
import maquette.core.values.user.EnvironmentContext;
import maquette.core.values.user.ProjectContext;
import maquette.core.values.user.User;

import java.time.Instant;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public final class LogEntry {

   Instant logged;

   String userId;

   UID application;

   EnvironmentContext environment;

   UID project;

   UID resource;

   Action action;

   public static LogEntry apply(User user, Action action) {
      return apply(user, action, null);
   }

   public static LogEntry apply(User user, Action action, UID resource) {
      return apply(
         Instant.now(),
         user.getDisplayName(),
         user.getApplicationContext().map(ApplicationContext::getId).orElse(null),
         user.getEnvironmentContext().orElse(null),
         user.getProjectContext().map(ProjectContext::getId).orElse(null),
         resource,
         action);
   }

}
