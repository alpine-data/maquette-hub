package maquette.common.postman;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Request {

   HttpMethod method;

   List<TextField> header;

   Body body;

}
