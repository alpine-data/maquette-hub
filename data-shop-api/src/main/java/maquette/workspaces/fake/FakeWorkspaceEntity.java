package maquette.workspaces.fake;

import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.workspaces.api.WorkspaceEntity;
import maquette.workspaces.api.WorkspaceProperties;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class FakeWorkspaceEntity implements WorkspaceEntity {

   WorkspaceProperties properties;

   @Override
   public UID getId() {
      return properties.getId();
   }

   @Override
   public CompletionStage<WorkspaceProperties> getProperties() {
      return CompletableFuture.completedFuture(properties);
   }

}
