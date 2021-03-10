package maquette.core.entities.logs.ports;

import akka.Done;
import maquette.core.entities.logs.LogEntryProperties;
import maquette.core.values.UID;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface AccessLogsRepository {

   CompletionStage<Done> append(LogEntryProperties entry);

   CompletionStage<List<LogEntryProperties>> getByProject(UID project);

   CompletionStage<List<LogEntryProperties>> getByUser(String userId);

   CompletionStage<List<LogEntryProperties>> getByResourcePrefix(UID resource);

}
