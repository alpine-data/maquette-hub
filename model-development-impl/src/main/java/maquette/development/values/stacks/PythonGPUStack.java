package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;

import java.util.List;

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
                    .apply("version", "3.11")
                    .withItem("3.11", "Python 3.11")
                    .withItem("3.10", "Python 3.10"))
            .withHelpText("Select the Python version you prefer.");

        var size = FormControl
            .apply(
                "Node size",
                InputPicker
                    .apply("size", "S")
                    .withItem("S", "Small")
                    .withItem("M", "Medium")
                    .withItem("L", "Large"))
            .withHelpText("The Node size specifies how many GPU cores and memory you have available.");

        return Form
            .apply()
            .withControl(version)
            .withControl(size);
    }

    @Override
    public Boolean isVolumeSupported() {
        return true;
    }
}