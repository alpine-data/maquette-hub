package maquette.core.server.commands;


import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class BooleanResult implements CommandResult {

    Boolean result;


}
