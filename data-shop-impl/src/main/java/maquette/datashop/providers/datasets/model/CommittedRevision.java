package maquette.datashop.providers.datasets.model;

import com.fasterxml.jackson.databind.JsonNode;
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
public class CommittedRevision implements Revision {

   UID id;

   ActionMetadata created;

   ActionMetadata modified;

   ActionMetadata committed;

   long records;

   Schema schema;

   DatasetVersion version;

   String message;

   JsonNode statistics;

   public static CommittedRevision apply(
      UID id, ActionMetadata created, ActionMetadata modified, ActionMetadata committed,
      long records, Schema schema, DatasetVersion version, String message) {

      return apply(id, created, modified, committed, records, schema, version, message, null);
   }

   @Override
   public Optional<CommittedRevision> getCommit() {
      return Optional.of(this);
   }

   public Optional<JsonNode> getStatistics() {
      return Optional.ofNullable(statistics);
   }
}
