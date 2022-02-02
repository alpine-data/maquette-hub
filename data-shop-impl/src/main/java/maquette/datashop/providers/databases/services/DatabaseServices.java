package maquette.datashop.providers.databases.services;

import akka.Done;
import maquette.core.values.user.User;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import maquette.datashop.providers.datasets.records.Records;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface DatabaseServices {

    CompletionStage<Done> analyze(User executor, String database);

    CompletionStage<Records> download(User executor, String database);

    CompletionStage<Optional<DatabaseAnalysisResult>> getAnalysisResult(User executor, String database);

}
