package maquette.datashop.providers.datasets.ports;

import com.fasterxml.jackson.databind.JsonNode;
import maquette.datashop.providers.datasets.model.DatasetVersion;

import java.util.concurrent.CompletionStage;

public interface DatasetDataExplorer {

   CompletionStage<JsonNode> analyze(String dataset, DatasetVersion version);

}
