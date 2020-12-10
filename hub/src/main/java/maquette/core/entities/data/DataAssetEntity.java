package maquette.core.entities.data;

import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.datasets.AccessRequests;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetProperties;

import java.util.concurrent.CompletionStage;

public interface DataAssetEntity<T extends DataAssetProperties<T>> {

   AccessRequests<T> getAccessRequests();

   MembersCompanion<DataAssetMemberRole> getMembers();

   UID getId();

   CompletionStage<T> getProperties();

}
