package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class CentralModelRegistryServicesSecured implements CentralModelRegistryServices {

    CentralModelRegistryServices delegate;

    WorkspaceServicesCompanion cmrCompanion;
    WorkspaceServicesCompanion companion;

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String search) {
        return delegate.getModels(user, search);
    }

    @Override
    public CompletionStage<Done> importModel(User user, String workspace, String model, String version) {
        return companion
            .withAuthorization(() -> companion.isMember(user, workspace, WorkspaceMemberRole.ADMIN))
            .thenCompose(ok -> delegate.importModel(user, workspace, model, version));
    }

    @Override
    public CompletionStage<Done> initialize() {
        return delegate.initialize();
    }
}
