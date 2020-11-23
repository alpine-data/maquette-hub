package maquette.core.entities.data.datasets.model.revisions;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.values.ActionMetadata;
import org.apache.avro.Schema;

import java.util.Optional;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class CommittedRevision implements Revision {

   String id;

   ActionMetadata created;

   ActionMetadata modified;

   ActionMetadata committed;

   long records;

   Schema schema;

   DatasetVersion version;

   String message;

   @Override
   public Optional<CommittedRevision> getCommit() {
      return Optional.of(this);
   }
}
