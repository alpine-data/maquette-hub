package maquette.core.entities.data.datasets.model.revisions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import org.apache.avro.Schema;

import java.util.Optional;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "state")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = OpenRevision.class, name = "open"),
      @JsonSubTypes.Type(value = CommittedRevision.class, name = "committed")
   })
public interface Revision {

   UID getId();

   ActionMetadata getCreated();

   ActionMetadata getModified();

   long getRecords();

   Schema getSchema();

   Optional<CommittedRevision> getCommit();

   Revision withModified(ActionMetadata modified);

   Revision withRecords(long newValue);

}
