package maquette.core.ports;

import akka.Done;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.entities.data.collections.model.CollectionTag;
import maquette.core.ports.common.DataAssetRepository;
import maquette.core.ports.common.HasDataAccessRequests;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CollectionsRepository extends DataAssetRepository<CollectionProperties>, HasDataAccessRequests, HasMembers<DataAssetMemberRole>, ObjectStore {

   CompletionStage<List<CollectionTag>> findAllTags(UID collection);

   CompletionStage<Optional<CollectionTag>> findTagByName(UID collection, String name);

   CompletionStage<Done> insertOrUpdateTag(UID collection, CollectionTag tag);

}
