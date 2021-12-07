package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * TODO: bn where is it used?
 * A simple value class to wrap configuration and run-time information of a stack instance.
 *
 * @param <T> The type of the stack's configuration.
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class StackInstanceProperties<T extends StackConfiguration> {

    Stack<T> stack;

    T configuration;

    StackInstanceParameters parameters;

}
