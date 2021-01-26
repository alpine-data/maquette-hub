package maquette.streams.core.entities.topic;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.streams.common.Operators;
import maquette.streams.core.entities.topic.requests.AppendRequest;
import maquette.streams.core.entities.topic.requests.CommitRequest;
import maquette.streams.core.entities.topic.requests.PollRequest;
import maquette.streams.core.entities.topic.requests.ReadRequest;
import maquette.streams.core.ports.RecordsRepository;
import maquette.streams.core.ports.TopicsRepository;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class TopicEntityImpl implements TopicEntity {

   private static final Duration RESEND_AFTER = Duration.ofMinutes(1);

   private final String name;

   private final TopicsRepository topics;

   private final RecordsRepository records;

   @Override
   public CompletionStage<Done> append(Record record) {
      return append(AppendRequest.apply(List.of(record)));
   }

   @Override
   public CompletionStage<Done> append(AppendRequest request) {
      return Operators
         .allOf(request
            .getRecords()
            .stream()
            .map(this.records::store))
         .thenCompose(done -> {
            var stored = request
               .getRecords()
               .stream()
               .map(r -> StoredRecord.apply(r.getKey()))
               .collect(Collectors.toList());

            return topics.insertOrUpdateStoredRecords(name, stored);
         });
   }

   @Override
   public CompletionStage<List<Record>> read(ReadRequest request) {
      return topics
         .getStoredRecords(name)
         .thenCompose(stored -> {
            var ids = stored
               .stream()
               .limit(request.getMaxCount())
               .map(StoredRecord::getId)
               .collect(Collectors.toList());

            return records.read(ids);
         });
   }

   @Override
   public CompletionStage<List<Record>> poll(PollRequest request) {
      return topics
         .getStoredRecords(name)
         .thenCompose(stored -> {
            var updated = stored
               .stream()
               .filter(storedRecord -> storedRecord.isOpenFor(request.getConsumerGroup(), RESEND_AFTER))
               .limit(request.getMaxCount())
               .map(storedRecord -> storedRecord.withSent(request.getConsumerGroup()))
               .collect(Collectors.toList());

            return topics
               .insertOrUpdateStoredRecords(name, updated)
               .thenApply(d -> updated);
         })
         .thenCompose(stored -> {
            var ids = stored
               .stream()
               .map(StoredRecord::getId)
               .collect(Collectors.toList());

            return records.read(ids);
         });
   }

   @Override
   public CompletionStage<Done> commit(CommitRequest request) {
      return topics
         .getStoredRecords(name)
         .thenCompose(stored -> {
            var updated = stored
               .stream()
               .filter(storedRecord -> request.getIds().contains(storedRecord.getId()))
               .map(storedRecord -> storedRecord.withCommitted(request.getConsumerGroup()))
               .collect(Collectors.toList());

            return topics.insertOrUpdateStoredRecords(name, updated);
         });
   }

}
