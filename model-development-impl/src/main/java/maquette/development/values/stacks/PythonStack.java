package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class PythonStack implements Stack<PythonStackConfiguration> {

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
    public String getIcon() {
        return STACK_NAME;
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
                    .withItem("38", "Python 3.8")
                    .withItem("37", "Python 3.7"))
            .withHelpText("Select the Python version you prefer.");

        return Form.apply().withControl(version);
    }

    @Override
    public CompletionStage<StackInstanceParameters> getParameters(PythonStackConfiguration configuration) {
        return null;
    }
}