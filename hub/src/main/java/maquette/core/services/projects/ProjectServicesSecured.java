package maquette.core.services.projects;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetProperties;
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
         var message = "Only authenticated users are allowed to create new projects";
         return CompletableFuture.failedFuture(NotAuthorizedException.apply(message));
      }
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String project) {
      return companion.withMembership(user, project, () -> delegate.environment(user, project));
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> getDataAssets(User user, String project) {
      return companion.withMembership(user, project, () -> delegate.getDataAssets(user, project));
   }

   @Override
   public CompletionStage<List<ProjectProperties>> list(User user) {
      return delegate.list(user);
   }

   @Override
   public CompletionStage<ProjectDetails> get(User user, String name) {
      return delegate.get(user, name);
   }

   @Override
   public CompletionStage<Done> remove(User user, String name) {
      return companion.withMembership(user, name, () -> delegate.remove(user, name));
   }

   @Override
   public CompletionStage<Done> update(User user, String name, String updatedName, String title, String summary) {
      return companion.withMembership(user, name, () -> delegate.remove(user, name));
   }

   @Override
   public CompletionStage<GrantedAuthorization> grant(User user, String name, Authorization authorization) {
      return companion.withMembership(user, name, () -> delegate.grant(user, name, authorization));
   }

   @Override
   public CompletionStage<Done> revoke(User user, String name, Authorization authorization) {
      return companion.withMembership(user, name, () -> delegate.revoke(user, name, authorization));
   }

}
