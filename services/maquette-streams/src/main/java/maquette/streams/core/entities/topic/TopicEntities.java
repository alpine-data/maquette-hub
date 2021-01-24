package maquette.streams.core.entities.topic;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.streams.common.Operators;
import maquette.streams.core.entities.topic.exceptions.TopicDoesNotExist;
import maquette.streams.core.ports.RecordsRepository;
import maquette.streams.core.ports.TopicsRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class TopicEntities {

   TopicsRepository topicsRepository;

   RecordsRepository recordsRepository;

   Map<String, TopicEntity> topics;

   public static TopicEntities apply(TopicsRepository topicRepository, RecordsRepository recordRepository) {
      var map = Maps.<String, TopicEntity>newHashMap();

      Operators
         .suppressExceptions(() -> topicRepository.getTopics().toCompletableFuture().get())
         .forEach(t -> map.put(t.getName(), createEntity(t.getName(), topicRepository, recordRepository)));

      return apply(topicRepository, recordRepository, map);
   }

   private static TopicEntity createEntity(String name, TopicsRepository topics, RecordsRepository records) {
      return TopicEntityAsync.apply(TopicEntityImpl.apply(name, topics, records));
   }

   public synchronized CompletionStage<Done> createTopic(TopicProperties properties) {
      if (!topics.containsKey(properties.getName())) {
         return topicsRepository
            .insertOrtUpdateTopic(properties)
            .thenApply(done -> {
               topics.put(properties.getName(), createEntity(properties.getName(), topicsRepository, recordsRepository));
               return done;
            });
      } else {
         return CompletableFuture.completedFuture(Done.getInstance());
      }
   }

   public CompletionStage<TopicEntity> getTopic(String name) {
      if (topics.containsKey(name)) {
         return CompletableFuture.completedFuture(topics.get(name));
      } else {
         return CompletableFuture.failedFuture(TopicDoesNotExist.apply(name));
      }
   }

   public CompletionStage<List<String>> getTopics() {
      return CompletableFuture.completedFuture(List.copyOf(topics.keySet()));
   }

}
