package maquette.core.ports.common;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.data.logs.DataAccessLogEntryProperties;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface HasAccessLogs {

   CompletionStage<Done> appendAccessLogEntry(DataAccessLogEntryProperties entry);

   CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByAsset(UID asset);

   CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByUser(String userId);

   CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByProject(UID project);

   CompletionStage<List<DataAccessLogEntryProperties>> findAllAccessLogs();

}
