package maquette.datashop.values.access_requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.WorkspaceProperties;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAccessRequest {

   private static final String ID = "id";
   private static final String CREATED = "created";
   private static final String WORKSPACE = "workspace";
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

   @JsonProperty(WORKSPACE)
   WorkspaceProperties workspace;

   @JsonProperty(EVENTS)
   List<DataAccessRequestEvent> events;

   @JsonProperty(CAN_GRANT)
   boolean canGrant;

   @JsonProperty(CAN_REQUEST)
   boolean canRequest;

   @JsonCreator
   public static DataAccessRequest apply(
      @JsonProperty(ID) UID id,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(ASSET) DataAssetProperties asset,
      @JsonProperty(WORKSPACE) WorkspaceProperties workspace,
      @JsonProperty(EVENTS) List<DataAccessRequestEvent> events) {

      if (events.isEmpty()) {
         throw new IllegalArgumentException("events may not be empty");
      }

      List<DataAccessRequestEvent> eventsCopy = events
         .stream()
         .sorted(Comparator.comparing(DataAccessRequestEvent::getEventMoment).reversed())
         .collect(Collectors.toList());

      return new DataAccessRequest(id, created, asset, workspace, eventsCopy, false, false);
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
