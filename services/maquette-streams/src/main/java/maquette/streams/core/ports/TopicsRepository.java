package maquette.streams.core.ports;

import akka.Done;
import maquette.streams.core.entities.topic.StoredRecord;
import maquette.streams.core.entities.topic.TopicProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TopicsRepository {

   CompletionStage<Optional<TopicProperties>> findTopicByName(String name);

   CompletionStage<List<StoredRecord>> getStoredRecords(String topic);

   CompletionStage<List<TopicProperties>> getTopics();

   CompletionStage<Done> insertOrtUpdateTopic(TopicProperties topic);

   CompletionStage<Done> insertOrUpdateStoredRecords(String topic, List<StoredRecord> record);

}
