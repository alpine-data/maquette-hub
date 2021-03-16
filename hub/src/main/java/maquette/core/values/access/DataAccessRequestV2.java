package maquette.core.values.access;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.data.assets_v2.model.DataAssetProperties;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAccessRequestV2 {

   private static final String ID = "id";
   private static final String CREATED = "created";
   private static final String PROJECT = "project";
   private static final String EVENTS = "events";
   private static final String STATUS = "status";
   private static final String TARGET_PROJECT = "target-project";
   private static final String ASSET = "asset";
   private static final String CAN_GRANT = "can-grant";
   private static final String CAN_REQUEST = "can-request";

   @JsonProperty(ID)
   UID id;

   @JsonProperty(CREATED)
   ActionMetadata created;

   @JsonProperty(ASSET)
   DataAssetProperties asset;

   @JsonProperty(PROJECT)
   ProjectProperties project;

   @JsonProperty(EVENTS)
   List<DataAccessRequestEvent> events;

   @JsonProperty(CAN_GRANT)
   boolean canGrant;

   @JsonProperty(CAN_REQUEST)
   boolean canRequest;

   @JsonCreator
   public static DataAccessRequestV2 apply(
      @JsonProperty(ID) UID id,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(ASSET) DataAssetProperties asset,
      @JsonProperty(PROJECT) ProjectProperties project,
      @JsonProperty(EVENTS) List<DataAccessRequestEvent> events) {

      if (events.isEmpty()) {
         throw new IllegalArgumentException("events may not be empty");
      }

      List<DataAccessRequestEvent> eventsCopy = events
         .stream()
         .sorted(Comparator.comparing(DataAccessRequestEvent::getEventMoment).reversed())
         .collect(Collectors.toList());

      return new DataAccessRequestV2(id, created, asset, project, eventsCopy, false, false);
   }

   @JsonProperty("actions")
   public Set<DataAccessRequestAction> getActions() {
      return DataAccessRequestCompanion.getActions(events, canGrant, canRequest);
   }

   @JsonProperty("status")
   public DataAccessRequestStatus getStatus() {
      return DataAccessRequestCompanion.getStatus(events);
   }

}
