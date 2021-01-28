package maquette.streams.core.entities.topic.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.streams.core.entities.topic.Record;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RecordsResult {

   List<Record> records;

}
