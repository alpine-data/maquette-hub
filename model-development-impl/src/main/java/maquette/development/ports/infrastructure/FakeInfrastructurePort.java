package maquette.development.ports.infrastructure;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;
import maquette.development.values.stacks.StackInstanceStatus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class FakeInfrastructurePort implements InfrastructurePort {

    @Override
    public CompletionStage<Done> createOrUpdateStackInstance(UID workspace, StackConfiguration configuration) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeStackInstance(String name) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> checkState(boolean forceUpdate) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<StackInstanceParameters> getInstanceParameters(UID workspace, String name) {
        return CompletableFuture.completedFuture(StackInstanceParameters
            .encodeAndCreate("http://foo", "MLFlow Dashboard")
            .withParameter("ENTRY_POINT_ENDPOINT", "http://foo")
            .withParameter("MLFLOW_ENDPOINT_LABEL", "MLflow Dashboard")
            .withParameter("CUSTOM_PARAM", "test"));
    }

    @Override
    public CompletionStage<StackInstanceStatus> getStackInstanceStatus(String name) {
        return CompletableFuture.completedFuture(StackInstanceStatus.DEPLOYED);
    }

    @Override
    public CompletionStage<Done> importModel(UID sourceWorkspace, String sourceModelName, String sourceModelVersion,
                                             UID destinationWorkspace, String destinationModelName,
                                             String destinationExperimentId, Map<String, String> sourceEnv, Map<String, String> destinationEnv) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
