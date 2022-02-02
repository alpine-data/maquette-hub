package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Composed value class of information read during runtime of a stack.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class StackRuntimeState {

    /**
     * Stack configuration how it has been deployed.
     */
    StackConfiguration configuration;

    /**
     * The deployment status of the stack.
     */
    StackInstanceStatus status;

    /**
     * Stack instance parameters. Might be incomplete if deployment has not been finished.
     */
    StackInstanceParameters parameters;

}
