package maquette.streams.core.services;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.streams.common.Operators;
import maquette.streams.common.records.Records;
import maquette.streams.core.entities.topic.Record;
import maquette.streams.core.entities.topic.TopicEntities;
import maquette.streams.core.entities.topic.TopicProperties;
import maquette.streams.core.entities.topic.requests.AppendRequest;
import maquette.streams.core.entities.topic.requests.CommitRequest;
import maquette.streams.core.entities.topic.requests.PollRequest;
import maquette.streams.core.entities.topic.requests.ReadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class TopicService {

   private final TopicEntities topics;

   public CompletionStage<Done> createTopic(TopicProperties topic) {
      return topics.createTopic(topic);
   }

   public CompletionStage<Done> append(String topic, Record record) {
      return topics.getTopic(topic).thenCompose(t -> t.append(record));
   }

   public CompletionStage<Done> append(String topic, AppendRequest request) {
      return topics.getTopic(topic).thenCompose(t -> t.append(request));
   }

   public CompletionStage<Done> append(String topic, Path avroRecords) {
      var records = Records
         .fromFile(avroRecords)
         .getRecords()
         .stream()
         .map(record -> Record.apply(Operators.randomHash(), Records.fromRecords(List.of(record))))
         .collect(Collectors.toList());

      return append(topic, AppendRequest.apply(records));
   }

   public CompletionStage<List<Record>> read(String topic, ReadRequest request) {
      return topics.getTopic(topic).thenCompose(t -> t.read(request));
   }

   public CompletionStage<InputStream> readAvro(String topic, ReadRequest request) {
      return read(topic, request).thenApply(records -> {
         var avroRecords = records
            .stream()
            .flatMap(record -> record.getRecord().getRecords().stream())
            .collect(Collectors.toList());

         var recordsCombined =Records.fromRecords(avroRecords);
         var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
         recordsCombined.toFile(file);

         return DeleteOnCloseInputStream.apply(file);
      });
   }

   public CompletionStage<List<Record>> poll(String topic, PollRequest request) {
      return topics.getTopic(topic).thenCompose(t -> t.poll(request));
   }

   public CompletionStage<Done> commit(String topic, CommitRequest request) {
      return topics.getTopic(topic).thenCompose(t -> t.commit(request));
   }

   public CompletionStage<List<String>> getTopics() {
      return topics.getTopics();
   }

   @AllArgsConstructor(access = AccessLevel.PRIVATE)
   public static class DeleteOnCloseInputStream extends InputStream {

      private final Path file;

      private final InputStream delegate;

      public static DeleteOnCloseInputStream apply(Path file) {
         return new DeleteOnCloseInputStream(file, Operators.suppressExceptions(() -> Files.newInputStream(file)));
      }

      @Override
      public int read() throws IOException {
         return delegate.read();
      }

      @Override
      public void close() throws IOException {
         delegate.close();
         Files.deleteIfExists(file);
      }
   }

}
