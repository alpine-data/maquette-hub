package maquette.asset_providers.sources.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.avro.Schema;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceProperties {

   long records;

   Schema schema;

}
