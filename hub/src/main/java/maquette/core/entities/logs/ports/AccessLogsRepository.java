package maquette.core.entities.logs.ports;

import akka.Done;
import maquette.core.entities.logs.LogEntry;
import maquette.core.values.UID;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface AccessLogsRepository {

   CompletionStage<Done> append(LogEntry entry);

   CompletionStage<List<LogEntry>> getByProject(UID project);

   CompletionStage<List<LogEntry>> getByUser(String userId);

   CompletionStage<List<LogEntry>> getByResourcePrefix(UID resource);

}
