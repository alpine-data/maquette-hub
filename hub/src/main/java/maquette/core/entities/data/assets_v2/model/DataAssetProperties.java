package maquette.core.entities.data.assets_v2.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetState;
import maquette.core.values.user.User;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetProperties {

   UID id;

   @With
   String type;

   @With
   DataAssetMetadata metadata;

   @With
   DataAssetState state;

   @With
   ActionMetadata created;

   ActionMetadata updated;

   public DataAssetProperties withUpdated(User user) {
      return withUpdated(ActionMetadata.apply(user));
   }

   public DataAssetProperties withUpdated(ActionMetadata updated) {
      return apply(id, type, metadata, state, created, updated);
   }

}
