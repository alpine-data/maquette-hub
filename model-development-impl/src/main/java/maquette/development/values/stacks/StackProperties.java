package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.forms.Form;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class StackProperties {

    String title;

    String name;

    String summary;

    List<String> tags;

    Form configurationForm;

    Boolean isVolumeSupported;

}
