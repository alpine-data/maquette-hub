package maquette.core.entities.data.assets_v2.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.data.*;
import maquette.core.values.user.User;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetProperties {

   UID id;

   String type;

   DataAssetMetadata metadata;

   DataAssetState state;

   ActionMetadata created;

   ActionMetadata updated;

   public DataAssetProperties withUpdated(User user) {
      return withUpdated(ActionMetadata.apply(user));
   }

}
