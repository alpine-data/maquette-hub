package maquette.core.entities.logs.ports;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.core.entities.logs.LogEntry;
import maquette.core.values.UID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileSystemAccessLogsRepository implements AccessLogsRepository {

   private static final Logger LOG = LoggerFactory.getLogger(AccessLogsRepository.class);

   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
      .withZone(ZoneId.systemDefault());

   private final Path directory;

   private final ObjectMapper om;

   public static FileSystemAccessLogsRepository apply(Path directory, ObjectMapper om) {
      var dir = directory.resolve("logs");
      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return new FileSystemAccessLogsRepository(dir, om);
   }

   public static FileSystemAccessLogsRepository apply(Path directory) {
      return apply(directory, ObjectMapperFactory.apply().create(true));
   }

   public static FileSystemAccessLogsRepository apply() {
      return apply(new File("data").toPath());
   }

   @Override
   public CompletionStage<Done> append(LogEntry entry) {
      var file = directory.resolve(String.format(
         "%s.log.json",
         DATE_TIME_FORMATTER.format(entry.getLogged())
      ));

      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), entry));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   private Stream<LogEntry> readAll() {
      return Operators.suppressExceptions(() -> Files.walk(directory))
         .filter(p -> p.getFileName().endsWith("log.json"))
         .map(p -> Operators.ignoreExceptionsWithDefault(
            () -> om.readValue(p.toFile(), LogEntry.class),
            null,
            LOG
         ))
         .filter(e -> !Objects.isNull(e));
   }

   @Override
   public CompletionStage<List<LogEntry>> getByProject(UID project) {
      return CompletableFuture.completedFuture(readAll()
         .filter(e -> e.getProject() != null && e.getProject().equals(project))
         .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<LogEntry>> getByUser(String userId) {
      return CompletableFuture.completedFuture(readAll()
         .filter(e -> e.getUserId() != null && e.getUserId().equals(userId))
         .collect(Collectors.toList()));
   }

   @Override
   public CompletionStage<List<LogEntry>> getByResourcePrefix(UID resource) {
      return CompletableFuture.completedFuture(readAll()
         .filter(e -> e.getResource() != null && e.getProject().getValue().startsWith(resource.getValue()))
         .collect(Collectors.toList()));
   }

}
