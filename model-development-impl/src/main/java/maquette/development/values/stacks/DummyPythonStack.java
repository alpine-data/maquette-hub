package maquette.development.values.stacks;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public class DummyPythonStack implements Stack<DummyPythonStackConfiguration> {

    public static final String STACK_NAME = "python";

    @Override
    public String getTitle() {
        return "Python";
    }

    @Override
    public String getName() {
        return "python";
    }

    @Override
    public String getSummary() {
        return "A simple Python runtime with a Jupyter Notebook for interactive data analysis.";
    }

    @Override
    public List<String> getTags() {
        return Lists.newArrayList("python", "ml", "analytics", "notebook");
    }

    @Override
    public Class<DummyPythonStackConfiguration> getConfigurationType() {
        return DummyPythonStackConfiguration.class;
    }

    @Override
    public Form getConfigurationForm() {
        var version = FormControl
            .apply(
                "Python Version",
                InputPicker
                    .apply("version", "3.8")
                    .withItem("3.8", "Python 3.8")
                    .withItem("3.7", "Python 3.7"))
            .withHelpText("Select the Python version you prefer.");

        return Form.apply().withControl(version);
    }

}
