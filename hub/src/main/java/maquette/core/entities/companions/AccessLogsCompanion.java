package maquette.core.entities.companions;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.ports.common.HasAccessLogs;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.logs.DataAccessLogEntryProperties;
import maquette.core.values.data.logs.DataAccessType;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class AccessLogsCompanion {

   private final UID id;

   private final HasAccessLogs repository;

   public CompletionStage<Done> log(User executor, UID project, DataAccessType accessType, String message) {
      var entry = DataAccessLogEntryProperties.apply(id, project, accessType, ActionMetadata.apply(executor), message);
      return repository.appendAccessLogEntry(entry);
   }

   public CompletionStage<Done> log(User executor, DataAccessType accessType, String message) {
      var entry = DataAccessLogEntryProperties.apply(id, accessType, ActionMetadata.apply(executor), message);
      return repository.appendAccessLogEntry(entry);
   }

   public CompletionStage<List<DataAccessLogEntryProperties>> getLogs() {
      return repository.findAccessLogsByAsset(id);
   }

}
