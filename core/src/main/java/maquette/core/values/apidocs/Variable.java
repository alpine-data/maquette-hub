package maquette.core.values.apidocs;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.common.Operators;

@Value
@AllArgsConstructor(staticName = "apply")
public class Variable {

    String id;

    String key;

    String value;

    public static Variable apply(String key, String value) {
        return apply(Operators.randomHash(), key, value);
    }

}
