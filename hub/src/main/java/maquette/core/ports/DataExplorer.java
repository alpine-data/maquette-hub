package maquette.core.ports;

import com.fasterxml.jackson.databind.JsonNode;
import maquette.core.entities.data.datasets.model.DatasetVersion;

import java.util.concurrent.CompletionStage;

public interface DataExplorer {

   CompletionStage<JsonNode> analyze(String dataset, DatasetVersion version);

}
