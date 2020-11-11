package maquette.core.values.access;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import maquette.core.values.ActionMetadata;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@With
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAccessRequest {

   private static final String ID = "id";
   private static final String CREATED = "created";
   private static final String ORIGIN = "origin";
   private static final String EVENTS = "events";

   @JsonProperty(ID)
   private final String id;

   @JsonProperty(CREATED)
   private final ActionMetadata created;

   @JsonProperty(ORIGIN)
   private final String origin;

   @JsonProperty(EVENTS)
   private final List<DataAccessRequestEvent> events;

   @JsonCreator
   public static DataAccessRequest apply(
      @JsonProperty(ID) String id,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(ORIGIN) String forProject,
      @JsonProperty(EVENTS) List<DataAccessRequestEvent> events) {

      if (events.isEmpty()) {
         throw new IllegalArgumentException("events may not be empty");
      }

      List<DataAccessRequestEvent> eventsCopy = events
         .stream()
         .sorted(Comparator.comparing(DataAccessRequestEvent::getEventMoment).reversed())
         .collect(Collectors.toList());

      return new DataAccessRequest(id, created, forProject, eventsCopy);
   }

   public static DataAccessRequest apply(String id, ActionMetadata created, String forProject, String reason) {
      var requested = Requested.apply(created, reason);
      return apply(id, created, forProject, List.of(requested));
   }

   public void addEvent(DataAccessRequestEvent event) {
      if (event.getEventMoment().isBefore(events.get(0).getEventMoment())) {
         throw new IllegalArgumentException("event may not be before previous event");
      }

      // TODO mw: Handle invalid state transitions

      this.events.add(0, event);
   }

   public List<DataAccessRequestEvent> getEvents() {
      return List.copyOf(events);
   }

   @JsonProperty("status")
   public DataAccessRequestStatus getStatus() {
      var latest = events.get(0);

      if (latest instanceof Requested) {
         return DataAccessRequestStatus.REQUESTED;
      } else if (latest instanceof Granted) {
         return DataAccessRequestStatus.GRANTED;
      } else if (latest instanceof Rejected) {
         return DataAccessRequestStatus.REJECTED;
      } else if (latest instanceof Expired) {
         return DataAccessRequestStatus.EXPIRED;
      } else if (latest instanceof Withdrawn) {
         return DataAccessRequestStatus.WITHDRAWN;
      } else {
         throw new IllegalStateException("Unknown event " + latest);
      }
   }

}
