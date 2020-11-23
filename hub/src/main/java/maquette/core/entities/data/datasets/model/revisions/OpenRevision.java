package maquette.core.entities.data.datasets.model.revisions;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import org.apache.avro.Schema;

import java.util.Optional;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class OpenRevision implements Revision {

   String id;

   ActionMetadata created;

   ActionMetadata modified;

   long records;

   Schema schema;

   @Override
   public Optional<CommittedRevision> getCommit() {
      return Optional.empty();
   }
}
