package maquette.development.values.stacks;

import com.google.common.collect.Lists;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.inputs.InputControl;

import java.util.List;
import java.util.concurrent.CompletionStage;

public final class MlflowStack implements Stack<MlflowStackConfiguration>{

    public static final String STACK_NAME = "mlflow";

    @Override
    public String getTitle() {
        return "MLflow";
    }

    @Override
    public String getName() {
        return "mlflow";
    }

    @Override
    public String getSummary() {
        return "A full-featured MLflow instance.";
    }

    @Override
    public String getIcon() {
        return "/foo.png";
    }

    @Override
    public List<String> getTags() {
        return Lists.newArrayList();
    }

    @Override
    public Class<MlflowStackConfiguration> getConfigurationType() {
        return MlflowStackConfiguration.class;
    }

    @Override
    public Form getConfigurationForm() {
        return Form.apply();
    }

    @Override
    public CompletionStage<StackInstanceParameters> getParameters(MlflowStackConfiguration configuration) {
        return null;
    }
}
