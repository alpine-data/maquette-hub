package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.server.CommandResult;

@Value
@AllArgsConstructor(staticName = "apply")
public class CollectionView implements CommandResult {

   Collection collection;

   boolean canAccessData;

   boolean isOwner;

   boolean isMember;

}
