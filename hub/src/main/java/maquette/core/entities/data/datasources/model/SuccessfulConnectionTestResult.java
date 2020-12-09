package maquette.core.entities.data.datasources.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.avro.Schema;

@Value
@AllArgsConstructor(staticName = "apply")
public class SuccessfulConnectionTestResult implements ConnectionTestResult {

   Schema schema;

   long records;

   @Override
   public String getResult() {
      return "success";
   }
}
