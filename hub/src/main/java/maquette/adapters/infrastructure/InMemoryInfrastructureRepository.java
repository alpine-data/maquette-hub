package maquette.adapters.infrastructure;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.entities.infrastructure.model.DeploymentMemento;
import maquette.core.ports.InfrastructureRepository;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryInfrastructureRepository implements InfrastructureRepository {

    private final Map<String, DeploymentMemento> deployments;

    public static InMemoryInfrastructureRepository apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateDeployment(DeploymentMemento memento) {
        deployments.put(memento.getConfig().getName(), memento);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeDeployment(String name) {
        deployments.remove(name);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<List<DeploymentMemento>> getDeployments() {
        return CompletableFuture.completedFuture(Lists.newArrayList(deployments.values().iterator()));
    }

}
