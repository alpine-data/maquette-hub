package maquette.adapters;

import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.operations.MaquetteModelOperations;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteModelOperationsAdapter implements ModelOperationsPort {

    /**
     * Maquette Runtime. Model Development module is only accessed within functions because during initialisation
     * of this adapter this module might not be available.
     */
    private final MaquetteRuntime runtime;

    @Override
    public CompletionStage<List<Object>> getServices(String modelUrl) {
        return runtime
            .getModule(MaquetteModelOperations.class)
            .getEntities()
            .findServicesByModelUrl(modelUrl)
            .thenApply(list -> list.stream()
                .map(m -> (Object) m)
                .collect(Collectors.toList())); // Just to make Java typing happy.
    }

}
