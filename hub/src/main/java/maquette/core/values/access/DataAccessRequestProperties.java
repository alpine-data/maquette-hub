package maquette.core.values.access;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@With
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAccessRequestProperties {

   private static final String ID = "id";
   private static final String CREATED = "created";
   private static final String ORIGIN = "origin";
   private static final String EVENTS = "events";
   private static final String ASSET = "asset";
   private static final String PROJECT = "project";

   @JsonProperty(ID)
   private final UID id;

   @JsonProperty(CREATED)
   private final ActionMetadata created;

   @JsonProperty(ASSET)
   private final UID asset;

   @JsonProperty(PROJECT)
   private final UID project;

   @JsonProperty(EVENTS)
   private final List<DataAccessRequestEvent> events;

   @JsonCreator
   public static DataAccessRequestProperties apply(
      @JsonProperty(ID) UID id,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(ASSET) UID asset,
      @JsonProperty(PROJECT) UID project,
      @JsonProperty(EVENTS) List<DataAccessRequestEvent> events) {

      if (events.isEmpty()) {
         throw new IllegalArgumentException("events may not be empty");
      }

      List<DataAccessRequestEvent> eventsCopy = events
         .stream()
         .sorted(Comparator.comparing(DataAccessRequestEvent::getEventMoment).reversed())
         .collect(Collectors.toList());

      return new DataAccessRequestProperties(id, created, asset, project, eventsCopy);
   }

   public static DataAccessRequestProperties apply(
      UID id, ActionMetadata created, UID asset, UID project, String reason) {
      var requested = Requested.apply(created, reason);
      return apply(id, created, asset, project, List.of(requested));
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

   public Set<DataAccessRequestAction> getActions(boolean canGrant, boolean canRequest) {
      return DataAccessRequestCompanion.getActions(events, canGrant, canRequest);
   }

   public DataAccessRequestStatus getStatus() {
      return DataAccessRequestCompanion.getStatus(events);
   }

}