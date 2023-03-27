package maquette.development.ports.infrastructure;

import akka.Done;
import maquette.core.values.UID;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;
import maquette.development.values.stacks.StackInstanceStatus;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface InfrastructurePort {

    /**
     * Add or update a stack configuration on linked infrastructure.
     *
     * @param workspace     The workspace the stack belongs to.
     * @param configuration The configuration.
     * @return Done.
     */
    CompletionStage<Done> createOrUpdateStackInstance(UID workspace,
                                                      StackConfiguration configuration);

    /**
     * Remove a stack configuration from the infrastructure.
     *
     * @param name The name of the stack configuration.
     * @return Done.
     */
    CompletionStage<Done> removeStackInstance(String name);

    /**
     * Re-deploy or check the current state.
     *
     * @return Done.
     */
    CompletionStage<Done> checkState();

    /**
     * Receive runtime parameters from infrastructure.
     *
     * @param workspace The workspace a stack belongs to.
     * @param name      The name of the stack configuration.
     * @return Runtime parameters of the deployment.
     */
    CompletionStage<StackInstanceParameters> getInstanceParameters(UID workspace, String name);

    /**
     * Return deployment status of a submitted stack.
     *
     * @param name The name of the stack configuration.
     * @return The current deployment status.
     */
    CompletionStage<StackInstanceStatus> getStackInstanceStatus(String name);


    /**
     * Import model from source workspace to destination workspace
     * @param sourceWorkspace source workspace
     * @param sourceModelName source model name
     * @param sourceModelVersion  source model version
     * @param destinationWorkspace destination workspace
     * @param destinationModelName destination model name
     * @param destinationExperimentId destination experiment id
     * @param sourceEnv source environment variables
     * @param destinationEnv destination environment variables
     * @return done
     */
    CompletionStage<Done> importModel(
                                      UID sourceWorkspace,
                                      String sourceModelName,
                                      String sourceModelVersion,
                                      UID destinationWorkspace,
                                      String destinationModelName,
                                      String destinationExperimentId,
                                      Map<String, String> sourceEnv,
                                      Map<String, String> destinationEnv);

}
