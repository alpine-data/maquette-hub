package maquette.adapters;

import lombok.AllArgsConstructor;
import maquette.development.ports.models.ModelOperationsPort;
import maquette.operations.MaquetteModelOperations;

import java.util.List;
import java.util.concurrent.CompletionStage;

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
            .getDeployedModelEntity(modelUrl)
            .thenApply(entity -> {
                // TODO: Implement List method on ModelEntity.
                return List.of();
            });
    }

    public void setMaquetteModule(MaquetteModelOperations module) {
        this.operations = module;
    }
}
