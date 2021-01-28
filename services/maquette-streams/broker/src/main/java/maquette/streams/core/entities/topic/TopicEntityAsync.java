package maquette.streams.core.entities.topic;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.streams.core.entities.topic.requests.AppendRequest;
import maquette.streams.core.entities.topic.requests.CommitRequest;
import maquette.streams.core.entities.topic.requests.PollRequest;
import maquette.streams.core.entities.topic.requests.ReadRequest;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor(access = AccessLevel.PRIVATE, staticName = "apply")
public final class TopicEntityAsync implements TopicEntity {

   private final TopicEntity delegate;

   private final ReentrantLock lock;

   public static TopicEntityAsync apply(TopicEntity entity) {
      return apply(entity, new ReentrantLock());
   }

   @Override
   public CompletionStage<Done> append(Record record) {
      lock.lock();

      try {
         var result = delegate.append(record);
         result.handle((d, t) -> {
            lock.unlock();
            return "";
         });
         return result;
      } catch (Exception e) {
         lock.unlock();
         throw e;
      }
   }

   @Override
   public CompletionStage<Done> append(AppendRequest request) {
      lock.lock();

      try {
         var result = delegate.append(request);
         result.handle((d, t) -> {
            lock.unlock();
            return "";
         });
         return result;
      } catch (Exception e) {
         lock.unlock();
         throw e;
      }
   }

   @Override
   public CompletionStage<List<Record>> read(ReadRequest request) {
      lock.lock();

      try {
         var result = delegate.read(request);
         result.handle((d, t) -> {
            lock.unlock();
            return "";
         });
         return result;
      } catch (Exception e) {
         lock.unlock();
         throw e;
      }
   }

   @Override
   public CompletionStage<List<Record>> poll(PollRequest request) {
      lock.lock();

      try {
         var result = delegate.poll(request);
         result.handle((d, t) -> {
            lock.unlock();
            return "";
         });
         return result;
      } catch (Exception e) {
         lock.unlock();
         throw e;
      }
   }

   @Override
   public CompletionStage<Done> commit(CommitRequest request) {
      lock.lock();

      try {
         var result = delegate.commit(request);
         result.handle((d, t) -> {
            lock.unlock();
            return "";
         });
         return result;
      } catch (Exception e) {
         lock.unlock();
         throw e;
      }
   }
}
