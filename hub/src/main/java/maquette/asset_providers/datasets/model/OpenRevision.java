package maquette.asset_providers.datasets.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import org.apache.avro.Schema;

import java.util.Optional;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class OpenRevision implements Revision {

   UID id;

   ActionMetadata created;

   ActionMetadata modified;

   long records;

   Schema schema;

   @Override
   public Optional<CommittedRevision> getCommit() {
      return Optional.empty();
   }
}
