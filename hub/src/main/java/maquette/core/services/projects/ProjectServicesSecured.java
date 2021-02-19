package maquette.core.services.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.Model;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.exceptions.NotAuthorizedException;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProjectServicesSecured implements ProjectServices {

   ProjectServices delegate;

   ProjectCompanion companion;

   @Override
   public CompletionStage<Done> create(User user, String name, String title, String summary) {
      if (user instanceof AuthenticatedUser) {
         return delegate.create(user, name, title, summary);
      } else {
         var message = "Only authenticated users are allowed to create new projects.";
         return CompletableFuture.failedFuture(NotAuthorizedException.apply(message));
      }
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String project, EnvironmentType type) {
      return companion
         .isAuthorized(() -> companion.isMember(user, project))
         .thenCompose(ok -> delegate.environment(user, project, type));
   }

   @Override
   public CompletionStage<List<ProjectProperties>> list(User user) {
      return delegate.list(user);
   }

   @Override
   public CompletionStage<Project> get(User user, String name) {
      return delegate
         .get(user, name)
         .thenCompose(project -> companion
            .filterAuthorized(project, () -> companion.isMember(user, name))
            .thenApply(opt -> opt.orElse(project
               .withMembers(List.of())
               .withSandboxes(List.of())
               .withDataAccessRequests(List.of()))));
   }

   @Override
   public CompletionStage<Done> remove(User user, String name) {
      return companion
         .isAuthorized(() -> companion.isMember(user, name, ProjectMemberRole.ADMIN))
         .thenCompose(ok -> delegate.remove(user, name));
   }

   @Override
   public CompletionStage<Done> update(User user, String name, String updatedName, String title, String summary) {
      return companion
         .isAuthorized(() -> companion.isMember(user, name, ProjectMemberRole.ADMIN))
         .thenCompose(ok -> delegate.update(user, name, updatedName, title, summary));
   }

   @Override
   public CompletionStage<List<Model>> getModels(User user, String name) {
      // TODO mw: Check auth
      return delegate.getModels(user, name);
   }

   @Override
   public CompletionStage<Done> grant(User user, String name, Authorization authorization, ProjectMemberRole role) {
      return companion
         .isAuthorized(() -> companion.isMember(user, name, ProjectMemberRole.ADMIN))
         .thenCompose(ok -> delegate.grant(user, name, authorization, role));
   }

   @Override
   public CompletionStage<Done> revoke(User user, String name, Authorization authorization) {
      return companion
         .isAuthorized(() -> companion.isMember(user, name, ProjectMemberRole.ADMIN))
         .thenCompose(ok -> delegate.revoke(user, name, authorization));
   }

}
