package maquette.core.entities.logs;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.logs.ports.AccessLogsRepository;
import maquette.core.entities.logs.ports.FileSystemAccessLogsRepository;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class AccessLogs {

   private final AccessLogsRepository port;

   public static AccessLogs apply() {
      return apply(FileSystemAccessLogsRepository.apply());
   }

   public CompletionStage<Done> log(LogEntry logEntry) {
      return port.append(logEntry);
   }

   public CompletionStage<Done> log(User user, Action action) {
      return log(LogEntry.apply(user, action));
   }

   public CompletionStage<Done> log(User user, Action action, UID resource) {
      return log(LogEntry.apply(user, action, resource));
   }

   public CompletionStage<List<LogEntry>> getByProject(UID project) {
      return port.getByProject(project);
   }

   public CompletionStage<List<LogEntry>> getByUser(String userId) {
      return port.getByUser(userId);
   }

   public CompletionStage<List<LogEntry>> getByResourcePrefix(UID resource) {
      return port.getByResourcePrefix(resource);
   }

}
