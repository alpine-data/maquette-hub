package maquette.datashop.providers.datasets.ports;

import akka.Done;
import maquette.datashop.providers.datasets.records.Records;

import java.util.concurrent.CompletionStage;

public interface RecordsStore {

   CompletionStage<Done> append(String key, Records records);

   CompletionStage<Done> clear(String key);

   CompletionStage<Records> get(String key);

}
