package maquette.core.values.apidocs;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileField implements Field {

   String key;

   String src;

}
