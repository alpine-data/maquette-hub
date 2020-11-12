package maquette.core.entities.datasets.model.revisions;

import maquette.core.values.ActionMetadata;
import org.apache.avro.Schema;

import java.util.Optional;

public interface Revision {

   String getId();

   ActionMetadata getCreated();

   ActionMetadata getModified();

   long getRecords();

   Schema getSchema();

   Optional<CommittedRevision> getCommit();

   Revision withModified(ActionMetadata modified);

   Revision withRecords(long newValue);

}
