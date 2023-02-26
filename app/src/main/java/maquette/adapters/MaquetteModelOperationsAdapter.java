package maquette.adapters;

import lombok.AllArgsConstructor;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.operations.MaquetteModelOperations;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteModelOperationsAdapter implements ModelOperationsPort {

    MaquetteModelOperations operations;

    public static MaquetteModelOperationsAdapter apply() {
        return apply(null);
    }

    @Override
    public CompletionStage<List<Object>> getServices(String modelUrl) {
        return operations
            .getEntities()
            .findServicesByModelUrl(modelUrl)
            .thenApply(list -> list.stream()
                .map(m -> (Object) m)
                .collect(Collectors.toList())); // Just to make Java typing happy.
    }

    public void setMaquetteModule(MaquetteModelOperations module) {
        this.operations = module;
    }
}
