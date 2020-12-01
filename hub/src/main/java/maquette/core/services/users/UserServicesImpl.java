package maquette.core.services.users;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.DatasetEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.services.datasets.DatasetCompanion;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class UserServicesImpl implements UserServices {

   private final DatasetEntities datasets;

   private final ProjectEntities projects;

   private final UserCompanion companion;

   private final DatasetCompanion datasetCompanion;

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

   @Override
   public CompletionStage<List<DataAssetProperties>> getDataAssets(User user) {
      return datasets
         .findDatasets()
         .thenApply(datasets1 -> datasets1
            .stream()
            .map(properties -> datasetCompanion.filterMember(user, properties.getName(), properties)))
         .thenCompose(Operators::allOf)
         .thenApply(datasets1 -> datasets1
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(properties -> (DataAssetProperties) properties)
            .collect(Collectors.toList()));
   }

}
