package maquette.core.server.commands.views;

import maquette.common.Operators;
import maquette.core.entities.users.model.UserProfile;
import maquette.core.services.ApplicationServices;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataAsset;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DataAssetViewCommandMixin {

   default CompletionStage<List<UserProfile>> getUserProfiles(
      User user, ApplicationServices services, DataAsset<?> asset, DataAssetMemberRole role) {

      return Operators.allOf(asset.getMembers(role)
         .stream()
         .map(GrantedAuthorization::getAuthorization)
         .filter(auth -> auth instanceof UserAuthorization)
         .map(auth -> (UserAuthorization) auth)
         .map(m -> services.getUserServices().getProfile(user, m.getName())));
   }

}
