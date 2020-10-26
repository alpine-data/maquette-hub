package maquette.core.values.access;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataAccessToken {

   ActionMetadata created;

   String name;

   String description;

   String key;

   String secret;

   public DataAccessTokenNarrowed toNarrowed() {
      return DataAccessTokenNarrowed.apply(created, name, description, key);
   }

}
