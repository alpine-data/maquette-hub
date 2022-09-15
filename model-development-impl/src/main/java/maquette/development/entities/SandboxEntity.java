package maquette.development.entities;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import maquette.core.values.UID;
import maquette.development.ports.SandboxesRepository;
import maquette.development.ports.WorkspacesRepository;
import maquette.development.ports.infrastructure.InfrastructurePort;
import maquette.development.values.EnvironmentType;
import maquette.development.values.sandboxes.SandboxProperties;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;
import maquette.development.values.stacks.StackRuntimeState;
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

    private final WorkspacesRepository workspaces;

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
                    var stack = Stacks
                        .apply()
                        .getStackByConfiguration(stackConfiguration);
                    var stackConfigurationName = String.format("mq--%s--%s--%s", workspace, id, stack.getName());
                    var workspacePropertiesCS = workspaces.getWorkspaceById(workspace);

                    return workspacePropertiesCS.thenCompose(workspaceProperties -> {
                        var updatedStackConfiguration = stackConfiguration
                            .withStackInstanceName(stackConfigurationName)
                            .withEnvironmentVariable("MQ_SANDBOX_ID", id.getValue())
                            .withEnvironmentVariable("MQ_WORKSPACE_ID", workspace.getValue())
                            .withEnvironmentVariable("MQ_WORKSPACE_NAME", workspaceProperties.getName());

                        return infrastructurePort
                            .createOrUpdateStackInstance(workspace, updatedStackConfiguration)
                            .thenApply(done -> updatedStackConfiguration);
                    });
                }))
            .thenApply(configurations -> configurations
                .stream()
                .collect(Collectors.toMap(StackConfiguration::getStackInstanceName, c -> c)))
            .thenCompose(
                stackConfigurationNames -> updateProperties(p -> p.withAdditionalStacks(stackConfigurationNames)));
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
    public CompletionStage<Map<String, StackInstanceParameters>> getStackInstanceParameters(
        EnvironmentType environmentType) {
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

    /**
     * Fetches all runtime information and returns the runtime state for the sandbox.
     *
     * @return The list of state information for all included stacks.
     */
    public CompletionStage<List<StackRuntimeState>> getState() {
        return getProperties()
            .thenCompose(properties -> Operators.allOf(properties
                .getStacks()
                .keySet()
                .stream()
                .map(stack -> {
                    var parametersCS = infrastructurePort.getInstanceParameters(workspace, stack);
                    var stateCS = infrastructurePort.getStackInstanceStatus(stack);

                    return Operators.compose(parametersCS, stateCS, (parameters, state) -> {
                        var config = properties
                            .getStacks()
                            .get(stack);
                        return StackRuntimeState.apply(config, state, parameters);
                    });
                })));
    }

    /**
     * Removes a sandbox and its related resources.
     *
     * @return Done.
     */
    public CompletionStage<Done> remove() {
        return getProperties().thenCompose(properties -> Operators
            .allOf(properties
                .getStacks()
                .keySet()
                .stream()
                .map(infrastructurePort::removeStackInstance))
            .thenCompose(ok -> sandboxes.removeSandboxById(workspace, id)));
    }

    private CompletionStage<Done> updateProperties(Function<SandboxProperties, SandboxProperties> updateFunc) {
        return getProperties()
            .thenCompose(p -> sandboxes.insertOrUpdateSandbox(workspace, updateFunc.apply(p)));
    }

}
