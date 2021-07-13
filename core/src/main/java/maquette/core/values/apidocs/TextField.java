package maquette.core.values.apidocs;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class TextField implements Field {

   String key;

   String value;

}
