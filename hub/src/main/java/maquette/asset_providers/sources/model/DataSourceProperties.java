package maquette.asset_providers.sources.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DataSourceProperties {

   long records;

   Schema schema;

   @Nullable
   JsonNode explorer;

   public static DataSourceProperties apply(long records, Schema schema) {
      return apply(records, schema, null);
   }

}
