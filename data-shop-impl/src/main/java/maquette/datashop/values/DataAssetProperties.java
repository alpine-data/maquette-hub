package maquette.datashop.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.values.metadata.DataAssetMetadata;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataAssetProperties {

   private static final String ID = "id";
   private static final String TYPE = "type";
   private static final String METADATA = "metadata";
   private static final String STATE = "state";
   private static final String CREATED = "created";
   private static final String UPDATED = "updated";

   @JsonProperty(ID)
   UID id;

   @With
   @JsonProperty(TYPE)
   String type;

   @With
   @JsonProperty(METADATA)
   DataAssetMetadata metadata;

   @With
   @JsonProperty(STATE)
   DataAssetState state;

   @With
   @JsonProperty(CREATED)
   ActionMetadata created;

   @JsonProperty(UPDATED)
   ActionMetadata updated;

   @JsonCreator
   public static DataAssetProperties apply(
      @JsonProperty(ID) UID id,
      @JsonProperty(TYPE) String type,
      @JsonProperty(METADATA) DataAssetMetadata metadata,
      @JsonProperty(STATE) DataAssetState state,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(UPDATED) ActionMetadata updated) {

      return new DataAssetProperties(id, type, metadata, state, created, updated);
   }

   public DataAssetProperties withUpdated(User user) {
      return withUpdated(ActionMetadata.apply(user));
   }

   public DataAssetProperties withUpdated(ActionMetadata updated) {
      return apply(id, type, metadata, state, created, updated);
   }

   public static DataAssetProperties fake(String name) {
      var action = ActionMetadata.apply("egon");
      return apply(UID.apply(), "fake", DataAssetMetadata.sample(name), DataAssetState.APPROVED, action, action);
   }

   public static DataAssetProperties fake() {
      return fake("fake");
   }

}
