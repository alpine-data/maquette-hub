package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.server.CommandResult;

@Value
@AllArgsConstructor(staticName = "apply")
public class StreamView implements CommandResult {

   Stream stream;

   boolean canAccessData;

   boolean isOwner;

   boolean isMember;

}
