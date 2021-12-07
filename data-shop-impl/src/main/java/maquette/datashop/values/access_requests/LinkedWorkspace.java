package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.UID;

@Value
@AllArgsConstructor(staticName = "apply")
public class LinkedWorkspace {

    UID id;

    JsonNode properties;

}
