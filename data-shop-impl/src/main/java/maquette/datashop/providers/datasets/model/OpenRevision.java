package maquette.datashop.providers.datasets.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import org.apache.avro.Schema;

import java.util.Optional;

@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenRevision implements Revision {

   private static final String ID = "_id";
   private static final String CREATED = "created";
   private static final String MODIFIED = "modified";
   private static final String RECORDS = "records";
   private static final String SCHEMA = "schema";

   @JsonProperty
   UID id;

   @JsonProperty
   ActionMetadata created;

   @JsonProperty
   ActionMetadata modified;

   @JsonProperty
   long records;

   @JsonProperty
   Schema schema;

   @JsonCreator
   public static OpenRevision apply(
           @JsonProperty(ID) UID id,
           @JsonProperty(CREATED) ActionMetadata created,
           @JsonProperty(MODIFIED) ActionMetadata modified,
           @JsonProperty(RECORDS) long records,
           @JsonProperty(SCHEMA) Schema schema) {

      return new OpenRevision(id, created, modified, records, schema);
   }

   @Override
   public Optional<CommittedRevision> getCommit() {
      return Optional.empty();
   }
}
