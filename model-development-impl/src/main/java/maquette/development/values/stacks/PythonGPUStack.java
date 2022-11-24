package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;
import maquette.core.modules.users.GlobalRole;
import maquette.development.configuration.StacksConfiguration;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor(staticName = "apply")
public final class PythonGPUStack implements Stack<PythonGPUStackConfiguration> {

    public static final String STACK_NAME = "python-gpu";

    @Override
    public String getTitle() {
        return "Python GPU";
    }

    @Override
    public String getName() {
        return STACK_NAME;
    }

    @Override
    public String getSummary() {
        return "A Python stack with an GPU infrastructure and nice pre-installed libraries.";
    }

    @Override
    public List<String> getTags() {
        return List.of("python", "notebook", "gpu");
    }

    @Override
    public Class<PythonGPUStackConfiguration> getConfigurationType() {
        return PythonGPUStackConfiguration.class;
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

        var config = StacksConfiguration.apply().getPythonGpu();
        var size = FormControl
            .apply(
                "GPU Memory",
                InputPicker
                    .apply("size", config.getSizeS().getSize())
                    .withItem(
                        config.getSizeS().getSize(),
                        String.format("Small (%s CHF/hour)", config.getSizeS().getPrice()),
                        String.format("Small includes %s GPU memory. This setup is preferred for experimental exploration.", config.getSizeS().getSizeString())
                    )
                    .withItem(
                        config.getSizeM().getSize(),
                        String.format("Medium (%s CHF/hour)", config.getSizeM().getPrice()),
                        String.format("Medium includes %s GPU memory. This setup is best used for normal training workloads.", config.getSizeM().getSizeString())
                    )
                    .withItem(
                        config.getSizeL().getSize(),
                        String.format("Large (%s CHF/hour)", config.getSizeL().getPrice()),
                        String.format("Large includes %s GPU memory. This setup is suggested for large scale machine learning.", config.getSizeL().getSizeString())
                    )
            )
            .withHelpText("Configure the GPU memory size of the stack instance.");

        return Form
            .apply()
            .withControl(version)
            .withControl(size);
    }

    @Override
    public Optional<GlobalRole> getRequiredRole() {
        return Optional.of(GlobalRole.ADVANCED_USER);
    }

    @Override
    public Boolean isVolumeSupported() {
        return true;
    }
}