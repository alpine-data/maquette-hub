package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.server.CommandResult;
import maquette.core.values.data.logs.DataAccessLogEntry;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class CollectionView implements CommandResult {

   Collection collection;

   List<DataAccessLogEntry> logs;

   boolean canAccessData;

   boolean canWrite;

   boolean isOwner;

   boolean isMember;

}
