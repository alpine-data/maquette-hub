package maquette.development.ports.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.development.values.model.services.ModelServiceProperties;

import java.util.List;
import java.util.concurrent.*;

/**
 * Stupid simple implementation of {@link ModelServingPort} which can be used in Unit-Tests.
 */
@AllArgsConstructor(staticName = "apply")
public final class InMemoryModelServing implements ModelServingPort {

    private List<ModelServiceProperties> services;

    /**
     * Creates a new instance.
     *
     * @return The instance;
     */
    public static InMemoryModelServing apply() {
        return apply(Lists.newArrayList());
    }

    @Override
    public CompletionStage<ModelServiceProperties> createModel(String modelName,
                                                               String modelVersion,
                                                               String serviceName,
                                                               String mlflowInstanceId,
                                                               String maintainerName,
                                                               String maintainerEmail) {

        var urlsMap = Maps.<String, String>newHashMap();
        urlsMap.put("Deployment Status", "http://some.url");
        urlsMap.put("Service Catalog", "http://some.url");
        urlsMap.put("Git Repository", "http://i-dont-know-yet.com");
        urlsMap.put("Build Pipeline", "http://i-dont-know-yet.com");

        var properties = ModelServiceProperties.apply(serviceName, urlsMap);
        this.services.add(properties);

        return CompletableFuture.completedFuture(properties);
    }

}
