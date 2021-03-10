package maquette.core.entities.data.assets;

import akka.Done;
import maquette.core.entities.companions.AccessLogsCompanion;
import maquette.core.entities.companions.MembersCompanion;
import maquette.core.entities.data.assets.AccessRequests;
import maquette.core.values.UID;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface DataAssetEntity<T extends DataAssetProperties<T>> {

   CompletionStage<Done> approve(User executor);

   CompletionStage<Done> deprecate(User executor, boolean deprecate);

   AccessLogsCompanion getAccessLogs();

   AccessRequests<T> getAccessRequests();

   MembersCompanion<DataAssetMemberRole> getMembers();

   UID getId();

   CompletionStage<T> getProperties();

}
