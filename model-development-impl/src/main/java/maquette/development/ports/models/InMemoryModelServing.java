package maquette.development.ports.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.development.values.model.services.ModelServiceProperties;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryModelServing implements ModelServingPort {

    private List<ModelServiceProperties> services;

    public static InMemoryModelServing apply() {
        return apply(Lists.newArrayList());
    }

    @Override
    public CompletionStage<ModelServiceProperties> createModel(String modelName,
                                                               String modelVersion,
                                                               String environment,
                                                               String serviceName,
                                                               String mlflowInstanceId,
                                                               String maintainerName,
                                                               String maintainerEmail) {

        var properties = ModelServiceProperties.apply(serviceName, Maps.newHashMap());
        this.services.add(properties);
        return CompletableFuture.completedFuture(properties);
    }

}
