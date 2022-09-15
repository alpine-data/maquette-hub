package maquette.datashop.providers.databases.ports;


import java.util.concurrent.CompletionStage;

public interface DatabaseDataExplorer {

    CompletionStage<DatabaseAnalysisResult> analyze(String database, String authTokenId, String authTokenSecret);

}
