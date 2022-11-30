package maquette.development.ports.models;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class FakeModelOperationsPort implements ModelOperationsPort {

    @Override
    public CompletionStage<List<Object>> getServices(String modelUrl) {
        return CompletableFuture.completedFuture(List.of());
    }

}
