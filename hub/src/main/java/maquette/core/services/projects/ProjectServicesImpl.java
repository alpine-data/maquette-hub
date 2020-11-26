package maquette.core.services.projects;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.Datasets;
import maquette.core.entities.infrastructure.InfrastructureManager;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.projects.Project;
import maquette.core.entities.projects.Projects;
import maquette.core.entities.projects.model.ProjectDetails;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.access.DataAccessRequestStatus;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.data.LinkedDataAsset;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ProjectServicesImpl implements ProjectServices {

   ProcessManager processes;

   Projects projects;

   Datasets datasets;

   InfrastructureManager infrastructure;

   ProjectCompanion companion;

   @Override
   public CompletionStage<Done> create(User executor, String name, String title, String summary) {
      return projects
         .createProject(executor, name, title, summary)
         .thenCompose(project -> project.grant(executor, executor.toAuthorization()))
         .thenApply(ignore -> Done.getInstance());
   }

   @Override
   public CompletionStage<Map<String, String>> environment(User user, String name) {
      return projects
         .findProjectByName(name)
         .thenCompose(maybeProject -> {
            if (maybeProject.isEmpty()) {
               throw new RuntimeException(String.format("No project found with name `%s`", name));
            }

            var project = maybeProject.get();
            var result = Maps.<String, String>newHashMap();

            result.put("MQ_PROJECT_ID", project.getId());

            return infrastructure
               .getDeployment(String.format("mq__%s", project.getId()))
               .flatMap(d -> d.getContainer(String.format("mq__%s__minio", project.getId())))
               .map(c -> c.getMappedPortUrls().thenApply(urls -> {
                  result.put("MINIO_URL", urls.get(9000).toString());
                  return result;
               }))
               .orElseGet(() -> CompletableFuture.completedFuture(result))
               .thenApply(m -> m);
         });
   }

   @Override
   public CompletionStage<List<DataAssetProperties>> getDataAssets(User user, String projectName) {
      return companion.withProject(projectName).thenCompose(project -> {
         var datasetsOwned = this.datasets.findDatasets(project.getId())
            .thenApply(properties -> properties
               .stream()
               .map(p -> (DataAssetProperties) p)
               .collect(Collectors.toList()));

         var datasetsLinked = this.datasets.findDataAccessRequestsByOrigin(project.getId())
            .thenCompose(requests -> Operators.allOf(requests
               .stream()
               .filter(r -> r.getStatus().equals(DataAccessRequestStatus.GRANTED))
               .map(requestDetails -> companion.mapDataAccessRequestToDetails(project, requestDetails)
                  .thenApply(maybeRequest -> maybeRequest.map(request -> {
                     var targetProject = request.getTargetProject();
                     var targetDataset = request.getTarget();
                     return LinkedDataAsset.apply(targetProject, targetDataset);
                  })))
               .collect(Collectors.toList())))
            .thenApply(list -> list
               .stream()
               .filter(Optional::isPresent)
               .map(Optional::get)
               .collect(Collectors.toList()));


         return Operators.compose(datasetsOwned, datasetsLinked, (owned, linked) -> {
            List<DataAssetProperties> lists = Lists.newArrayList();
            lists.addAll(owned);
            lists.addAll(linked);
            // lists.add(CollectionProperties.apply("123", "Some Collection", "some-collection", "lorem ipsum", "foo bar", DataVisibility.PUBLIC, DataClassification.INTERNAL, PersonalInformation.NONE, ActionMetadata.apply("foo"), ActionMetadata.apply("bar")));
            return lists;
         });
      });
   }

   @Override
   public CompletionStage<List<ProjectProperties>> list(User user) {
      return projects.getProjects();
   }

   @Override
   public CompletionStage<ProjectDetails> get(User user, String name) {
      return companion.withProject(name).thenCompose(Project::getDetails);
   }

   @Override
   public CompletionStage<Done> remove(User user, String name) {
      return projects
         .findProjectByName(name)
         .thenCompose(maybeProject -> {
            if (maybeProject.isPresent()) {
               var projectId = maybeProject.get().getId();

               return projects
                  .removeProject(projectId)
                  .thenCompose(done -> infrastructure.removeDeployment(String.format("mq__%s", projectId)));
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         });
   }

   @Override
   public CompletionStage<Done> update(User user, String name, String updatedName, String title, String summary) {
      return companion.withProject(name)
         .thenCompose(project -> project.updateDetails(user, updatedName, title, summary));
   }

   /*
    * Manage members
    */

   @Override
   public CompletionStage<GrantedAuthorization> grant(User user, String name, Authorization authorization) {
      return companion.withProject(name).thenCompose(p -> p.grant(user, authorization));
   }

   @Override
   public CompletionStage<Done> revoke(User user, String name, Authorization authorization) {
      return companion.withProject(name).thenCompose(p -> p.revoke(user, authorization));
   }

}