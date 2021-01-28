package maquette.streams.adapters;

import akka.Done;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import maquette.streams.common.Operators;
import maquette.streams.core.entities.topic.StoredRecord;
import maquette.streams.core.entities.topic.TopicProperties;
import maquette.streams.core.ports.TopicsRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class TopicsRepositoryAdapter implements TopicsRepository {

   private final ObjectMapper om;

   private final Path directory;

   public static TopicsRepositoryAdapter apply(ObjectMapper om) {
      var data = ConfigFactory.load().getString("maquette.streams.data");
      var dir = new File(data).toPath().normalize().resolve("topics");

      Operators.suppressExceptions(() -> Files.createDirectories(dir));
      return apply(om, dir);
   }

   @Override
   public CompletionStage<Optional<TopicProperties>> findTopicByName(String name) {
      var result = readTopics()
         .filter(t -> t.getName().equals(name))
         .findFirst();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<StoredRecord>> getStoredRecords(String topic) {
      var result = readStoredRecords(topic).collect(Collectors.toList());
      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<TopicProperties>> getTopics() {
      var result = readTopics().collect(Collectors.toList());
      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrtUpdateTopic(TopicProperties topic) {
      writeTopic(topic);
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> insertOrUpdateStoredRecords(String topic, List<StoredRecord> records) {
      var ids = records.stream().map(StoredRecord::getId).collect(Collectors.toList());
      var existing = readStoredRecords(topic)
         .filter(record -> !ids.contains(record.getId()));
      var updated = Stream.concat(existing, records.stream()).collect(Collectors.toList());

      var file = directory.resolve(topic + ".records.json");
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), updated));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   private Stream<TopicProperties> readTopics() {
      return Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isRegularFile)
         .filter(f -> f.toString().endsWith(".topic.json"))
         .map(f -> {
            try {
               var props = om.readValue(f.toFile(), TopicProperties.class);
               return Optional.ofNullable(props);
            } catch (Exception e) {
               return Optional.<TopicProperties>empty();
            }
         })
         .filter(Optional::isPresent)
         .map(Optional::get);
   }

   private Stream<StoredRecord> readStoredRecords(String topic) {
      return Operators
         .suppressExceptions(() -> Files.list(directory))
         .filter(Files::isRegularFile)
         .filter(f -> f.toString().endsWith(topic + ".records.json"))
         .map(f -> {
            try {
               var props = om.readValue(
                  f.toFile(),
                  new TypeReference<List<StoredRecord>>(){});
               return Optional.ofNullable(props);
            } catch (Exception e) {
               return Optional.<List<StoredRecord>>empty();
            }
         })
         .filter(Optional::isPresent)
         .map(Optional::get)
         .flatMap(Collection::stream);
   }

   private void writeTopic(TopicProperties properties) {
      var file = directory.resolve(properties.getName() + ".topic.json");
      Operators.suppressExceptions(() -> om.writeValue(file.toFile(), properties));
   }

}
