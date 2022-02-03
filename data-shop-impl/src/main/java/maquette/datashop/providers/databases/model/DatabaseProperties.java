package maquette.datashop.providers.databases.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.datashop.providers.databases.ports.DatabaseAnalysisResult;
import org.apache.avro.Schema;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DatabaseProperties {

   long records;

   Schema schema;

   @Nullable
   DatabaseAnalysisResult statistics;

   public static DatabaseProperties apply(long records, Schema schema) {
      return apply(records, schema, null);
   }

   public Optional<DatabaseAnalysisResult> getStatistics() {
      return Optional.ofNullable(statistics);
   }

}
