package maquette.development.values.model.services;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@AllArgsConstructor(staticName = "apply")
public class ModelServiceProperties {

    String name;

    /**
     * A set of links related to the model service.
     * The key of the map is a label for the link, the value the target.
     */
    Map<String, String> links;

}
