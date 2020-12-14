package maquette.core.ports;

import akka.Done;
import maquette.core.values.data.records.Records;

import java.util.concurrent.CompletionStage;

public interface RecordsStore {

   CompletionStage<Done> append(String key, Records records);

   CompletionStage<Done> clear(String key);

   CompletionStage<Records> get(String key);

}
