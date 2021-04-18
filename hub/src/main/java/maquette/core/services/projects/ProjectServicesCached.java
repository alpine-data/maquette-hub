package maquette.core.services.projects;

import akka.Done;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import maquette.core.entities.projects.model.Project;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.projects.model.apps.Application;
import maquette.core.entities.projects.model.model.Model;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.governance.CodeIssue;
import maquette.core.entities.projects.model.settings.WorkspaceGenerator;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.user.User;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class ProjectServicesCached implements ProjectServices {

   ProjectServices delegate;

   AsyncCache<Done, List<WorkspaceGenerator>> templatesCache;

   public static ProjectServicesCached apply(ProjectServices delegate) {
      AsyncCache<Done, List<WorkspaceGenerator>> templatesCache = Caffeine
         .newBuilder()
         .maximumSize(3)
         .expireAfterAccess(Duration.ofMinutes(5))
         .buildAsync();

      return apply(delegate, templatesCache);
   }

   @Override
   public CompletionStage<Done> create(User user, String name, String title, String summary) {
      return delegate.create(user, name, title, summary);
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String name, EnvironmentType environmentType) {
      return delegate.environment(user, name, environmentType);
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String name) {
      return delegate.environment(user, name);
   }

   @Override
   public CompletionStage<List<ProjectProperties>> list(User user) {
      return delegate.list(user);
   }

   @Override
   public CompletionStage<Project> get(User user, String name) {
      return delegate.get(user, name);
   }

   @Override
   public CompletionStage<Done> remove(User user, String name) {
      return delegate.remove(user, name);
   }

   @Override
   public CompletionStage<Done> update(User user, String name, String updatedName, String title, String summary) {
      return delegate.update(user, name, updatedName, title, summary);
   }

   @Override
   public CompletionStage<List<ModelProperties>> getModels(User user, String name) {
      return delegate.getModels(user, name);
   }

   @Override
   public CompletionStage<Model> getModel(User user, String project, String model) {
      return delegate.getModel(user, project, model);
   }

   @Override
   public CompletionStage<Done> updateModel(User user, String project, String model, String title, String description) {
      return delegate.updateModel(user, project, model, title, description);
   }

   @Override
   public CompletionStage<Done> updateModelVersion(User user, String project, String model, String version, String description) {
      return delegate.updateModelVersion(user, project, model, version, description);
   }

   @Override
   public CompletionStage<Done> answerQuestionnaire(User user, String project, String model, String version, JsonNode responses) {
      return delegate.answerQuestionnaire(user, project, model, version, responses);
   }

   @Override
   public CompletionStage<Done> approveModel(User user, String project, String model, String version) {
      return delegate.approveModel(user, project, model, version);
   }

   @Override
   public CompletionStage<Done> promoteModel(User user, String project, String model, String version, String stage) {
      return delegate.promoteModel(user, project, model, version, stage);
   }

   @Override
   public CompletionStage<Done> rejectModel(User user, String project, String model, String version, String reason) {
      return delegate.rejectModel(user, project, model, version, reason);
   }

   @Override
   public CompletionStage<Done> requestModelReview(User user, String project, String model, String version) {
      return delegate.requestModelReview(user, project, model, version);
   }

   @Override
   public CompletionStage<Done> reportCodeQuality(User user, String project, String model, String version, String commit, int score, int coverage, List<CodeIssue> issues) {
      return delegate.reportCodeQuality(user, project, model, version, commit, score, coverage, issues);
   }

   @Override
   public CompletionStage<Done> runExplainer(User user, String project, String model, String version) {
      return delegate.runExplainer(user, project, model, version);
   }

   @Override
   public CompletionStage<Optional<JsonNode>> getLatestQuestionnaireAnswers(User user, String project, String model) {
      return delegate.getLatestQuestionnaireAnswers(user, project, model);
   }

   @Override
   public CompletionStage<Done> grantModelRole(User user, String project, String model, UserAuthorization authorization, ModelMemberRole role) {
      return delegate.grantModelRole(user, project, model, authorization, role);
   }

   @Override
   public CompletionStage<Done> revokeModelRole(User user, String name, String model, UserAuthorization authorization) {
      return delegate.revokeModelRole(user, name, model, authorization);
   }

   @Override
   public CompletionStage<Done> createApplication(User user, String project, String name, String description, String gitRepository) {
      return delegate.createApplication(user, project, name, description, gitRepository);
   }

   @Override
   public CompletionStage<List<Application>> getApplications(User user, String project) {
      return delegate.getApplications(user, project);
   }

   @Override
   public CompletionStage<Done> removeApplication(User user, String project, String name) {
      return delegate.removeApplication(user, project, name);
   }

   @Override
   public CompletionStage<Done> grant(User user, String name, Authorization authorization, ProjectMemberRole role) {
      return delegate.grant(user, name, authorization, role);
   }

   @Override
   public CompletionStage<Done> revoke(User user, String name, Authorization authorization) {
      return delegate.revoke(user, name, authorization);
   }

   @Override
   public CompletionStage<List<WorkspaceGenerator>> listWorkspaceGenerators(User user) {
      return templatesCache.get(Done.getInstance(), (i, ex) -> delegate.listWorkspaceGenerators(user).toCompletableFuture());
   }

}
