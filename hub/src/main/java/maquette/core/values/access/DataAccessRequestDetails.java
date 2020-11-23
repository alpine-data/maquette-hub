package maquette.core.values.access;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.data.DataAssetProperties;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAccessRequestDetails {

   private static final String ID = "id";
   private static final String CREATED = "created";
   private static final String ORIGIN = "origin";
   private static final String EVENTS = "events";
   private static final String STATUS = "status";
   private static final String TARGET_PROJECT = "target-project";
   private static final String TARGET = "target";
   private static final String CAN_GRANT = "can-grant";
   private static final String CAN_REQUEST = "can-request";

   @JsonProperty(ID)
   String id;

   @JsonProperty(CREATED)
   ActionMetadata created;

   @JsonProperty(TARGET_PROJECT)
   ProjectProperties targetProject;

   @JsonProperty(TARGET)
   DataAssetProperties target;

   @JsonProperty(ORIGIN)
   ProjectProperties origin;

   @JsonProperty(EVENTS)
   List<DataAccessRequestEvent> events;

   @JsonProperty(STATUS)
   DataAccessRequestStatus status;

   @JsonProperty(CAN_GRANT)
   boolean canGrant;

   @JsonProperty(CAN_REQUEST)
   boolean canRequest;

   @JsonCreator
   public static DataAccessRequestDetails apply(
      @JsonProperty(ID) String id,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(TARGET_PROJECT) ProjectProperties targetProject,
      @JsonProperty(TARGET) DataAssetProperties target,
      @JsonProperty(ORIGIN) ProjectProperties origin,
      @JsonProperty(EVENTS) List<DataAccessRequestEvent> events,
      @JsonProperty(STATUS) DataAccessRequestStatus status,
      @JsonProperty(CAN_GRANT) boolean canGrant,
      @JsonProperty(CAN_REQUEST) boolean canRequest) {

      if (events.isEmpty()) {
         throw new IllegalArgumentException("events may not be empty");
      }

      List<DataAccessRequestEvent> eventsCopy = events
         .stream()
         .sorted(Comparator.comparing(DataAccessRequestEvent::getEventMoment).reversed())
         .collect(Collectors.toList());

      return new DataAccessRequestDetails(id, created, targetProject, target, origin, eventsCopy, status, canGrant, canRequest);
   }

   @JsonProperty("actions")
   public Set<DataAccessRequestAction> getActions() {
      var result = Sets.<DataAccessRequestAction>newHashSet();
      var latest = events.get(0);

      if (latest instanceof Requested) {
         if (canGrant) {
            result.add(DataAccessRequestAction.RESPOND);
         } else if (canRequest) {
            result.add(DataAccessRequestAction.WITHDRAW);
         }
      }

      if (latest instanceof Granted) {
         result.add(DataAccessRequestAction.WITHDRAW);
      }

      if (latest instanceof Rejected || latest instanceof Withdrawn || latest instanceof Expired) {
         if (canRequest) {
            result.add(DataAccessRequestAction.REQUEST);
         }
      }

      return result;
   }

}
