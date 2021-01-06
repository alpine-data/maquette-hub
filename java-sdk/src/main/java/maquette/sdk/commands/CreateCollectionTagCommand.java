package maquette.sdk.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class CreateCollectionTagCommand implements Command {

   String collection;

   String tag;

   String message;

   @JsonProperty("command")
   public String getCommand() {
      return "collections tag";
   }

}
