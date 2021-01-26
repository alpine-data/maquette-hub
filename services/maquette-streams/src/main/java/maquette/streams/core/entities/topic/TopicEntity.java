package maquette.streams.core.entities.topic;

import akka.Done;
import maquette.streams.core.entities.topic.requests.AppendRequest;
import maquette.streams.core.entities.topic.requests.CommitRequest;
import maquette.streams.core.entities.topic.requests.PollRequest;
import maquette.streams.core.entities.topic.requests.ReadRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TopicEntity {

   CompletionStage<Done> append(Record record);

   CompletionStage<Done> append(AppendRequest request);

   CompletionStage<List<Record>> read(ReadRequest request);

   CompletionStage<List<Record>> poll(PollRequest request);

   CompletionStage<Done> commit(CommitRequest request);

}
