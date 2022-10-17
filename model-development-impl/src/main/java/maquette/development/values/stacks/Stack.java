package maquette.development.values.stacks;

import com.github.javafaker.Bool;
import maquette.core.common.forms.Form;

import java.util.List;

/**
 * A stack defines a set of tools (e.g. Python, R-Studio, MLFlow) which can be instantiated within a sandbox. Each stack
 * type (classes implementing this interface) can defined its own custom configuration. Custom configuration are
 * parameters
 * which are set by the user when creating the stack.
 *
 * @param <T> The type of the stack configuration.
 */
public interface Stack<T extends StackConfiguration> {

    String getTitle();

    String getName();

    String getSummary();

    List<String> getTags();

    Class<T> getConfigurationType();

    Form getConfigurationForm();

    default Boolean isVolumeSupported() {
        return false;
    }

    default StackProperties getProperties() {
        return StackProperties.apply(getTitle(), getName(), getSummary(), getTags(), getConfigurationForm(), isVolumeSupported());
    }

}
