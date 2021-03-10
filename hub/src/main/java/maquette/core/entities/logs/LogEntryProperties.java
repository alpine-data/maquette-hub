package maquette.core.entities.logs;

import lombok.*;
import maquette.core.values.UID;
import maquette.core.values.user.ApplicationContext;
import maquette.core.values.user.EnvironmentContext;
import maquette.core.values.user.ProjectContext;
import maquette.core.values.user.User;

import java.time.Instant;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class LogEntryProperties {

   Instant logged;

   String userId;

   UID application;

   EnvironmentContext environment;

   UID project;

   UID resource;

   Action action;

   public static LogEntryProperties apply(User user, Action action) {
      return apply(user, action, null);
   }

   public static LogEntryProperties apply(User user, Action action, UID resource) {
      return apply(
         Instant.now(),
         user.getDisplayName(),
         user.getApplicationContext().map(ApplicationContext::getId).orElse(null),
         user.getEnvironmentContext().orElse(null),
         user.getProjectContext().map(ProjectContext::getId).orElse(null),
         resource,
         action);
   }

   public Optional<UID> getApplication() {
      return Optional.ofNullable(application);
   }

   public Optional<EnvironmentContext> getEnvironment() {
      return Optional.ofNullable(environment);
   }

   public Optional<UID> getProject() {
      return Optional.ofNullable(project);
   }

   public Optional<UID> getResource() {
      return Optional.ofNullable(resource);
   }

}
