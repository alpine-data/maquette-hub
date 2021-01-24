package maquette.streams.core.entities.topic;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.apache.commons.compress.utils.Lists;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class StoredRecord {

   /**
    * The record's unique id.
    */
   String id;

   /**
    * The moment when the record was inserted.
    */
   Instant inserted;

   /**
    * A map tracking the moments when the record has been sent the last time to a consumer group.
    */
   Map<String, Instant> sent;

   /**
    * A map containing the consumer ids which have committed the record already.
    */
   List<String> committedBy;

   public static StoredRecord apply(String id) {
      return apply(id, Instant.now(), Maps.newHashMap(), Lists.newArrayList());
   }

   public boolean isOpenFor(String consumerGroup, Duration resendAfterDuration) {
      return !(wasSent(consumerGroup, resendAfterDuration) || wasCommitted(consumerGroup));
   }

   public StoredRecord withCommitted(String consumerGroup) {
      if (!committedBy.contains(consumerGroup)) {
         committedBy.add(consumerGroup);
      }

      return this;
   }

   public StoredRecord withSent(String consumerGroup) {
      sent.put(consumerGroup, Instant.now());
      return this;
   }

   public boolean wasSent(String consumerGroup, Duration within) {
      if (sent.containsKey(consumerGroup)) {
         var moment = sent.get(consumerGroup);
         return Duration.between(moment, Instant.now()).compareTo(within) <= 0;
      } else {
         return false;
      }
   }

   public boolean wasCommitted(String consumerGroup) {
      return committedBy.contains(consumerGroup);
   }

}
