package maquette.core.values.user;

import lombok.AllArgsConstructor;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.WildcardAuthorization;

import java.util.List;

/**
 * The internal service user is a special user which is used by Maquette services to authenticate requests.
 * These requests are usually not recorded/ logged. The service user is like an Admin user which has all rights.
 */
@AllArgsConstructor(staticName = "apply")
public final class SystemUser implements User {

   @Override
   public String getDisplayName() {
      return "Maquette System User";
   }

   @Override
   public List<String> getRoles() {
      return List.of();
   }

   @Override
   public Authorization toAuthorization() {
      return WildcardAuthorization.apply();
   }

   @Override
   public boolean isSystemUser() {
      return true;
   }

}
