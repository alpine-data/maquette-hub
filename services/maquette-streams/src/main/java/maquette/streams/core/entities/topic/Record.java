package maquette.streams.core.entities.topic;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.streams.common.records.Records;

@Value
@AllArgsConstructor(staticName = "apply")
public class Record {

   String key;

   Records record;

}
