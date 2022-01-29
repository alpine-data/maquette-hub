package maquette.development.values.stacks;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class SynapseStack implements Stack<SynapseStackConfiguration> {

    public static final String STACK_NAME = "synapse";

    @Override
    public String getTitle() {
        return "Azure Synapse";
    }

    @Override
    public String getName() {
        return STACK_NAME;
    }

    @Override
    public String getSummary() {
        return "Azure Synapse Workspace to analyze data with T-SQL and Spark.";
    }

    @Override
    public List<String> getTags() {
        return Lists.newArrayList("analytics", "python", "scala", "sql");
    }

    @Override
    public Class<SynapseStackConfiguration> getConfigurationType() {
        return SynapseStackConfiguration.class;
    }

    @Override
    public Form getConfigurationForm() {
        var version = FormControl
            .apply(
                "Spark Cluster Size",
                InputPicker
                    .apply("sparkSize", "none")
                    .withItem("none", "None")
                    .withItem("S", "Small")
                    .withItem("M", "Medium")
                    .withItem("L", "Large"))
            .withHelpText("Select a size for the integrated Spark Cluster.");

        return Form.apply().withControl(version);
    }
}
