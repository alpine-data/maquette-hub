package maquette.development.values.stacks;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class MlflowStack implements Stack<MlflowStackConfiguration> {

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

}
