package maquette.common.postman;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class FileField implements Field {

   String key;

   String src;

}
