package maquette.core.common.forms.inputs;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataItem {

    String value;

    String label;

    String description;

    public static DataItem apply(String key, String label) {
        return apply(key, label, null);
    }

}
