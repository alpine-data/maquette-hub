package maquette.core.services.users;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesImpl implements UserServices {

   private final ProjectEntities projects;

   private final UserCompanion companion;

   /*
    * Notifications
    */

   @Override
   public CompletionStage<List<UserNotification>> getNotifications(User executor) {
      return companion.withUserOrDefault(
         executor,
         Lists.newArrayList(),
         maquette.core.entities.users.User::getNotifications);
   }

   @Override
   public CompletionStage<Done> readNotification(User executor, String notificationId) {
      return companion.withUserOrDefault(
         executor,
         Done.getInstance(),
         user -> user.readNotification(notificationId));
   }

   /*
    * Assets
    */

   @Override
   public CompletionStage<List<ProjectProperties>> getProjects(User user) {
      return projects.getProjectsByMember(user);
   }

}
