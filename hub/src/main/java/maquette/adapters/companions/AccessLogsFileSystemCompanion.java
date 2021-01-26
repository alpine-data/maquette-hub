package maquette.adapters.companions;

import akka.Done;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.ports.common.HasAccessLogs;
import maquette.core.values.UID;
import maquette.core.values.data.logs.DataAccessLogEntryProperties;
import org.apache.commons.compress.utils.Lists;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessLogsFileSystemCompanion implements HasAccessLogs {

   private static final String FILE = "logs.json";

   private final Path directory;

   private final ObjectMapper om;

   private final JavaType type;

   public static AccessLogsFileSystemCompanion apply(Path directory, ObjectMapper om) {
      var type = om.getTypeFactory().constructCollectionLikeType(List.class, DataAccessLogEntryProperties.class);
      return new AccessLogsFileSystemCompanion(directory, om, type);
   }

   private Path getFile(UID parent) {
      return directory
         .resolve(parent.getValue())
         .resolve(FILE);
   }

   private List<DataAccessLogEntryProperties> load(Path file) {
      if (Files.exists(file)) {
         return Operators.suppressExceptions(() -> om.readValue(file.toFile(), type));
      } else {
         return List.of();
      }
   }

   private List<DataAccessLogEntryProperties> load(UID parent) {
      var file = getFile(parent);
      return load(file);
   }

   private void save(UID parent, List<DataAccessLogEntryProperties> logs) {
      Operators.suppressExceptions(() -> om.writeValue(getFile(parent).toFile(), logs));
   }

   @Override
   public CompletionStage<Done> appendAccessLogEntry(DataAccessLogEntryProperties entry) {
      var logs = load(entry.getAsset());
      var newLogs = Lists.<DataAccessLogEntryProperties>newArrayList();
      newLogs.addAll(logs);
      newLogs.add(entry);
      save(entry.getAsset(), newLogs);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByAsset(UID asset) {
      var logs = load(asset);
      return CompletableFuture.completedFuture(logs);
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByUser(String userId) {
      return findAllAccessLogs()
         .thenApply(logs -> logs
         .stream()
         .filter(log -> log.getAccessed().getBy().equals(userId))
         .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAccessLogsByProject(UID project) {
      return findAllAccessLogs()
         .thenApply(logs -> logs
            .stream()
            .filter(log -> log.getProject().isPresent() && log.getProject().get().equals(project))
            .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<DataAccessLogEntryProperties>> findAllAccessLogs() {
      var result = Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isDirectory)
         .map(dir -> dir.resolve(FILE))
         .filter(Files::isRegularFile)
         .map(this::load)
         .flatMap(Collection::stream)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }
}
