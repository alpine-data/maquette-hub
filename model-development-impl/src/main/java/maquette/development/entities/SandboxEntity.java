package maquette.development.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.development.ports.InfrastructurePort;
import maquette.development.ports.SandboxesRepository;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;
import maquette.development.values.stacks.Stacks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class SandboxEntity {

    private final SandboxesRepository sandboxes;

    private final InfrastructurePort infrastructurePort;

    private final UID id;

    private final UID workspace;

    /**
     * Uses the infrastructure port to deploy the configured stacks for this sandbox.
     *
     * @param stacks The list of stacks to be added to the sandbox.
     * @return Done.
     */
    public CompletionStage<Done> addStacks(List<StackConfiguration> stacks) {
        return Operators
            .allOf(stacks
                .stream()
                .map(stackConfiguration -> {
                    var stack = Stacks.apply().getStackByConfiguration(stackConfiguration);
                    var stackConfigurationName = String.format("mq--%s--%s--%s--%s", workspace, id, stack.getName(),
                        UID.apply(4));

                    var updatedStackConfiguration = stackConfiguration.withStackInstanceName(stackConfigurationName);

                    return infrastructurePort
                        .createOrUpdateStackInstance(workspace, updatedStackConfiguration)
                        .thenApply(done -> updatedStackConfiguration);
                }))
            .thenApply(configurations -> configurations
                .stream()
                .collect(Collectors.toMap(StackConfiguration::getStackInstanceName, c -> c)))
            .thenCompose(stackConfigurationNames -> updateProperties(p -> p.withAdditionalStacks(stackConfigurationNames)));
    }

    /**
     * Read and return sandbox properties.
     *
     * @return The Sandbox' properties.
     */
    public CompletionStage<SandboxProperties> getProperties() {
        return sandboxes.getSandboxById(workspace, id);
    }

    /**
     * Fetches current stack instance parameters from its running stacks.
     *
     * @return A map containing all stacks mapped to their current instance parameters.
     */
    public CompletionStage<Map<String, StackInstanceParameters>> getStackInstanceParameters() {
        return getProperties()
            .thenCompose(properties -> Operators.allOf(properties
                .getStacks()
                .keySet()
                .stream()
                .map(stack -> infrastructurePort
                    .getInstanceParameters(workspace, stack)
                    .thenApply(params -> Pair.of(stack, params)))))
            .thenApply(list -> list
                .stream()
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
    }

    private CompletionStage<Done> updateProperties(Function<SandboxProperties, SandboxProperties> updateFunc) {
        return getProperties()
            .thenCompose(p -> sandboxes.insertOrUpdateSandbox(workspace, updateFunc.apply(p)));
    }

}
