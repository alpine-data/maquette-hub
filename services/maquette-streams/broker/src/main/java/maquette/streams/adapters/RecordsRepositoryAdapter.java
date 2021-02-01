package maquette.streams.adapters;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import maquette.streams.common.Operators;
import maquette.streams.core.entities.topic.Record;
import maquette.streams.core.ports.RecordsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class RecordsRepositoryAdapter implements RecordsRepository {

   private static final Logger LOG = LoggerFactory.getLogger("maquette.streams");

   private final ObjectMapper om;

   private final Path directory;

   public static RecordsRepositoryAdapter apply(ObjectMapper om) {
      var data = ConfigFactory.load().getString("maquette.streams.data");
      var dir = new File(data).toPath().normalize().resolve("records");

      Operators.suppressExceptions(() -> Files.createDirectories(dir));

      LOG.info("Using `" + dir.toAbsolutePath().toString() + "` to store records.");
      return apply(om, dir);
   }

   private Path getFile(String id) {
      return directory.resolve(id + ".json");
   }

   @Override
   public CompletionStage<Done> delete(List<String> ids) {
      ids
         .stream()
         .map(this::getFile)
         .forEach(f -> Operators.suppressExceptions(() -> Files.deleteIfExists(f)));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> store(Record record) {
      var f = getFile(record.getKey());
      Operators.suppressExceptions(() -> om.writeValue(f.toFile(), record));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<List<Record>> read(List<String> ids) {
      var result = ids
         .stream()
         .map(this::getFile)
         .map(f -> {
            try {
               var record = om.readValue(f.toFile(), Record.class);
               return Optional.of(record);
            } catch (Exception e) {
               return Optional.<Record>empty();
            }
         })
         .filter(Optional::isPresent)
         .map(Optional::get)
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

}
