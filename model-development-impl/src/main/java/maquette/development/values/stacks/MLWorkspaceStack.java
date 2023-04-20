package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.inputs.InputPicker;
import maquette.development.configuration.StacksConfiguration;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class MLWorkspaceStack implements Stack<MLWorkspaceStackConfiguration> {

    public static final String STACK_NAME = "ml-workspace";

    @Override
    public String getTitle() {
        return "ML Workspace";
    }

    @Override
    public String getName() {
        return STACK_NAME;
    }

    @Override
    public String getSummary() {
        return "All-in-one web-based development environment for machine learning.";
    }

    @Override
    public List<String> getTags() {
        return List.of("python", "notebook", "ide");
    }

    @Override
    public Class<MLWorkspaceStackConfiguration> getConfigurationType() {
        return MLWorkspaceStackConfiguration.class;
    }

    @Override
    public Form getConfigurationForm() {
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
                    .withItem(
                        config.getMemoryRequestL().getMemoryRequest(),
                        String.format("Large (%s CHF/hour)", config.getMemoryRequestL().getPrice()),
                        String.format("Large includes %s memory. This setup is suggested for large scale machine learning.", config.getMemoryRequestL().getMemoryRequestString())
                    )
            )
            .withHelpText("Select the memory size of the stack instance.");


        return Form
            .apply()
            .withControl(memoryRequest);
    }

    @Override
    public Boolean isVolumeSupported() {
        return true;
    }
}