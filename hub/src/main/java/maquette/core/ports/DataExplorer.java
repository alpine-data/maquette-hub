package maquette.core.ports;

import com.fasterxml.jackson.databind.JsonNode;
import maquette.asset_providers.datasets.model.DatasetVersion;

import java.util.concurrent.CompletionStage;

public interface DataExplorer {

   CompletionStage<JsonNode> analyze(String dataset, DatasetVersion version);

}
