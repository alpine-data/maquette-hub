package maquette.development.services;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.user.User;
import maquette.development.values.EnvironmentType;
import maquette.development.values.WorkSpaceMlflowView;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.model.Model;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class CentralModelRegistryServicesSecured implements CentralModelRegistryServices {

    CentralModelRegistryServices delegate;

    WorkspaceServicesCompanion cmrCompanion;
    WorkspaceServicesCompanion companion;

    @Override
    public CompletionStage<Model> getModel(User user, String modelName) {
        return delegate.getModel(user, modelName);
    }

    @Override
    public CompletionStage<List<ModelProperties>> getModels(User user, String search) {
        return delegate.getModels(user, search);
    }

    @Override
    public CompletionStage<String> getExplainer(User user, String workspace, String model, String version, String artifactPath) {
        return delegate.getExplainer(user, workspace, model, version, artifactPath);
    }

    @Override
    public CompletionStage<WorkSpaceMlflowView> getWorkspaceForView() {
        return delegate.getWorkspaceForView();
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

    @Override
    public CompletionStage<Map<String, String>> getEnvironment(User user, String workspace, EnvironmentType environmentType, boolean returnBase64) {
        return delegate.getEnvironment(user, workspace, environmentType, returnBase64);
    }
}
