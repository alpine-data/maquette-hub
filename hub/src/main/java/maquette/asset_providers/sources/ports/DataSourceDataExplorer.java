package maquette.asset_providers.sources.ports;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.CompletionStage;

public interface DataSourceDataExplorer {

   CompletionStage<JsonNode> analyze(String source);

}
