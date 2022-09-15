package maquette.core.common.forms;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.forms.inputs.InputControl;

@Value
@AllArgsConstructor(staticName = "apply")
public class FormControl {

    String label;

    InputControl control;

    String helpText;

    public static FormControl apply(String label, InputControl control) {
        return apply(label, control, null);
    }

    public FormControl withHelpText(String helpText) {
        return apply(label, control, helpText);
    }

}
