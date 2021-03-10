package maquette.core.entities.logs;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.logs.ports.AccessLogsRepository;
import maquette.core.entities.logs.ports.FileSystemAccessLogsRepository;
import maquette.core.values.UID;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Logs {

   private final AccessLogsRepository port;

   public static Logs apply() {
      return apply(FileSystemAccessLogsRepository.apply());
   }

   public CompletionStage<Done> log(LogEntryProperties logEntry) {
      return port.append(logEntry);
   }

   public CompletionStage<Done> log(User user, Action action) {
      if (user.isSystemUser()) {
         return CompletableFuture.completedFuture(Done.getInstance());
      }

      return log(LogEntryProperties.apply(user, action));
   }

   public CompletionStage<Done> log(User user, Action action, UID resource) {
      if (user.isSystemUser()) {
         return CompletableFuture.completedFuture(Done.getInstance());
      }

      return log(LogEntryProperties.apply(user, action, resource));
   }

   public CompletionStage<List<LogEntryProperties>> getByProject(UID project) {
      return port.getByProject(project);
   }

   public CompletionStage<List<LogEntryProperties>> getByUser(String userId) {
      return port.getByUser(userId);
   }

   public CompletionStage<List<LogEntryProperties>> getByResourcePrefix(UID resource) {
      return port.getByResourcePrefix(resource);
   }

}
