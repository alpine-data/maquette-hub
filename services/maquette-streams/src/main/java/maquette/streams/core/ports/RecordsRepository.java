package maquette.streams.core.ports;

import akka.Done;
import maquette.streams.common.records.Records;
import maquette.streams.core.entities.topic.Record;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface RecordsRepository {

   CompletionStage<Done> delete(List<String> ids);

   CompletionStage<Done> store(Record record);

   CompletionStage<List<Record>> read(List<String> ids);

}
