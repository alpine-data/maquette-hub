package maquette.datashop.providers.datasets.ports;

import maquette.datashop.providers.datasets.model.DatasetVersion;

import java.util.concurrent.CompletionStage;

public interface DatasetDataExplorer {

   CompletionStage<AnalysisResult> analyze(String dataset, DatasetVersion version, String authTokenId, String authTokenSecret);

}
