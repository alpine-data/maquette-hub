package maquette.core.services.data;

import akka.Done;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface MemberServices {

   CompletionStage<Done> grant(User executor, String asset, Authorization member, DataAssetMemberRole role);

   CompletionStage<Done> revoke(User executor, String asset, Authorization member);

}
