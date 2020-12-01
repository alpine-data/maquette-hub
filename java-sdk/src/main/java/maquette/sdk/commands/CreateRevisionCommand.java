package maquette.sdk.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.avro.Schema;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateRevisionCommand {

   String dataset;

   Schema schema;

   @JsonProperty
   public String getCommand() {
      return "datasets revisions create";
   }

}
