package maquette.datashop.providers.datasets.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

   private static final String ID = "id";
   private static final String CREATED = "created";
   private static final String MODIFIED = "modified";
   private static final String COMMITTED = "committed";
   private static final String RECORDS = "records";
   private static final String SCHEMA = "schema";
   private static final String VERSION = "version";
   private static final String MESSAGE = "message";
   private static final String STATISTICS = "statistics";

   @JsonProperty(ID)
   UID id;

   @JsonProperty(CREATED)
   ActionMetadata created;

   @JsonProperty(MODIFIED)
   ActionMetadata modified;

   @JsonProperty(COMMITTED)
   ActionMetadata committed;

   @JsonProperty(RECORDS)
   long records;

   @JsonProperty(SCHEMA)
   Schema schema;

   @JsonProperty(VERSION)
   DatasetVersion version;

   @JsonProperty(MESSAGE)
   String message;

   @JsonProperty(STATISTICS)
   JsonNode statistics;

   @JsonCreator
   public static CommittedRevision apply(
      @JsonProperty(ID) UID id,
      @JsonProperty(CREATED) ActionMetadata created,
      @JsonProperty(MODIFIED) ActionMetadata modified,
      @JsonProperty(COMMITTED) ActionMetadata committed,
      @JsonProperty(RECORDS) long records,
      @JsonProperty(SCHEMA) Schema schema,
      @JsonProperty(VERSION) DatasetVersion version,
      @JsonProperty(MESSAGE) String message) {

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
