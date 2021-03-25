package maquette.core.server.commands.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.entities.data.model.DataAsset;
import maquette.core.entities.projects.model.ProjectProperties;
import maquette.core.server.Command;
import maquette.core.server.CommandResult;
import maquette.core.server.views.CreateDataAccessRequestView;
import maquette.core.services.ApplicationServices;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.data.DataClassification;
import maquette.core.values.exceptions.DomainException;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CreateDataAccessRequestViewCommand implements Command {

   String asset;

   String assetType;

   @Override
   public CompletionStage<CommandResult> run(User user, RuntimeConfiguration runtime, ApplicationServices services) {
      CompletionStage<DataAsset> assetCS = services
         .getDataAssetServices()
         .get(user, asset);

      var projectsCS = services
         .getUserServices()
         .getProjects(user);

      return Operators.compose(assetCS, projectsCS, (asset, projects) -> {
         var existingRequests = asset
            .getAccessRequests()
            .stream()
            .map(DataAccessRequest::getProject)
            .map(ProjectProperties::getId)
            .collect(Collectors.toList());

         var availableProjects = projects
            .stream()
            .filter(p -> existingRequests.stream().noneMatch(id -> p.getId().equals(id)))
            .collect(Collectors.toList());

         var isOwner = asset.isMember(user, DataAssetMemberRole.OWNER);
         var requiresExplicitApproval = !asset
            .getProperties()
            .getMetadata()
            .getClassification()
            .equals(DataClassification.PUBLIC);

         return CreateDataAccessRequestView.apply(asset, availableProjects, isOwner, requiresExplicitApproval);
      });
   }

   @Override
   public Command example() {
      return apply("some-asset", "datasource");
   }

   public static class UnknownAssetTypeException extends RuntimeException implements DomainException {

      private UnknownAssetTypeException(String assetType) {
         super(String.format("Unknown asset type `%s`", assetType));
      }

   }

}
