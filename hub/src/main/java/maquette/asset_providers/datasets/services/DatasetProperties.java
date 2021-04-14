package maquette.asset_providers.datasets.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DatasetProperties {

   JsonNode explo;

}
