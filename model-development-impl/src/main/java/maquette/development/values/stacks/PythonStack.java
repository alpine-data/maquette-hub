package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;
import maquette.development.configuration.StacksConfiguration;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class PythonStack implements Stack<PythonStackConfiguration> {

    public static final String STACK_NAME = "python";

    @Override
    public String getTitle() {
        return "Python";
    }

    @Override
    public String getName() {
        return STACK_NAME;
    }

    @Override
    public String getSummary() {
        return "A default python stack with Python Kernel, Miniconda and Jupyter.";
    }

    @Override
    public List<String> getTags() {
        return List.of("python", "notebook");
    }

    @Override
    public Class<PythonStackConfiguration> getConfigurationType() {
        return PythonStackConfiguration.class;
    }

    @Override
    public Form getConfigurationForm() {
        var version = FormControl
            .apply(
                "Python Version",
                InputPicker
                    .apply("version", "38")
                    .withItem("38", "Python 3.8"))
            .withHelpText("Select the Python version you prefer.");

        var config = StacksConfiguration.apply().getPython();
        var memoryRequest = FormControl
            .apply(
                "Memory",
                InputPicker
                    .apply("memoryRequest", config.getMemoryRequestS().getMemoryRequest())
                    .withItem(
                        config.getMemoryRequestS().getMemoryRequest(),
                        String.format("Small (%s CHF/hour)", config.getMemoryRequestS().getPrice()),
                        String.format("Small includes %s memory. This setup is preferred for experimental exploration.", config.getMemoryRequestS().getMemoryRequestString())
                    )
                    .withItem(
                        config.getMemoryRequestM().getMemoryRequest(),
                        String.format("Medium (%s CHF/hour)", config.getMemoryRequestM().getPrice()),
                        String.format("Medium includes %s memory. This setup is best used for normal training workloads.", config.getMemoryRequestM().getMemoryRequestString())
                    )
            )
            .withHelpText("Select the memory size of the stack instance.");


        return Form
            .apply()
            .withControl(version)
            .withControl(memoryRequest);
    }

    @Override
    public Boolean isVolumeSupported() {
        return true;
    }
}