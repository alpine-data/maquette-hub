package maquette.development.entities;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.core.ports.MembersCompanion;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.ports.ModelsRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.exceptions.WorkspaceNotFoundException;
import maquette.development.values.stacks.MlflowStackConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class WorkspaceEntity {

   private final UID id;

   private final WorkspacesRepository repository;

   private final ModelsRepository models;

   private final InfrastructurePort infrastructurePort;

   public MembersCompanion<WorkspaceMemberRole> members() {
      return MembersCompanion.apply(id, repository);
   }

   public CompletionStage<Boolean> isMember(User user) {
      return isMember(user, null);
   }

   public CompletionStage<Boolean> isMember(User user, WorkspaceMemberRole role) {
      return members()
         .getMembers()
         .thenApply(members -> members
            .stream()
            .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role))));
   }

   public CompletionStage<Done> updateProperties(User executor, String name, String title, String summary) {
      // TODO mw: value validation ...

      return getProperties()
         .thenCompose(properties -> {
            var updated = properties
               .withName(name)
               .withTitle(title)
               .withSummary(summary)
               .withModified(ActionMetadata.apply(executor));

            return repository.insertOrUpdateWorkspace(updated);
         });
   }

   public CompletionStage<Done> initializeMlflowEnvironment() {
      var config = MlflowStackConfiguration.apply(
          getMlflowStackName(id),
          Instant.now().plus(24, ChronoUnit.HOURS),
          Lists.newArrayList(getWorkspaceResourceGroupName()));

      return infrastructurePort.createOrUpdateStackInstance(id, config);
   }

   public CompletionStage<Map<String, String>> getEnvironment() {
      return infrastructurePort.getInstanceParameters(id, getMlflowStackName(id)).thenApply(parameters -> {
         Map<String, String> result = Maps.newHashMap();
         result.put("ENTRY_POINT_LABEL", parameters.getEntrypointLabel());
         result.put("ENTRY_POINT_ENDPOINT", parameters.getEntrypoint().toString());
         parameters.getParameters().forEach((key, value) -> result.put(key, value.toString()));
         return result;
      });
   }

   public CompletionStage<WorkspaceProperties> getProperties() {
      return repository
         .findWorkspaceById(id)
         .thenApply(opt -> opt.orElseThrow(() -> WorkspaceNotFoundException.applyFromId(id)));
   }

   public CompletionStage<ModelEntities> getModels() {
      return getProperties()
         .thenApply(WorkspaceProperties::getMlFlowConfiguration)
         .thenApply(Optional::orElseThrow)
         .thenApply(configuration -> ModelEntities.apply(id, configuration, models));
   }

   public static String getMlflowStackName(UID id) {
      return String.format("mlflow--%s", id.getValue());
   }

   private String getWorkspaceResourceGroupName() {
      return String.format("workspaces--%s", this.id);
   }

}
