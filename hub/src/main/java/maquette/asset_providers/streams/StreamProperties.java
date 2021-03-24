package maquette.asset_providers.streams;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.streams.model.DurationUnit;
import org.apache.avro.Schema;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class StreamProperties {

   int retentionDuration;

   DurationUnit retentionUnit;

   Schema schema;

}
