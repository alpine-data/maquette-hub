package maquette.core.entities.projects.model.model;

import lombok.AllArgsConstructor;
import maquette.core.entities.projects.model.ProjectMemberRole;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.data.DataAssetPermissions;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class ModelMembersCompanion {

   private final List<GrantedAuthorization<ModelMemberRole>> members;

   private final List<GrantedAuthorization<ProjectMemberRole>> projectMembers;

   public ModelPermissions getDataAssetPermissions(User user) {
      var isOwner = this.isMember(user, ModelMemberRole.OWNER) || this.isProjectMember(user, ProjectMemberRole.ADMIN);
      var isReviewer = this.isMember(user, ModelMemberRole.REVIEWER);
      var isDataScientist = this.isMember(user, ModelMemberRole.DATA_SCIENTIST) ||
         this.isProjectMember(user, ProjectMemberRole.MEMBER);

      return ModelPermissions.apply(isOwner, isReviewer, isDataScientist);
   }

   /**
    * Check whether a user has a member role of the model version.
    *
    * @param user The user to check.
    * @param role The role the user should have.
    * @return True if the user is a member with the given role.
    */
   public boolean isMember(User user, ModelMemberRole role) {
      return members
         .stream()
         .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role)));
   }

   /**
    * Check whether a user is a member with any role of the model version.
    *
    * @param user The user to check.
    * @return True if the user a member of the data asset.
    */
   public boolean isMember(User user) {
      return isMember(user, null);
   }

   /**
    * Check whether a user has a member role of the model.
    *
    * @param user The user to check.
    * @param role The role the user should have.
    * @return True if the user is a member with the given role.
    */
   public boolean isProjectMember(User user, ProjectMemberRole role) {
      return projectMembers
         .stream()
         .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role)));
   }

   /**
    * Check whether a user is a member with any role of the model.
    *
    * @param user The user to check.
    * @return True if the user a member of the data asset.
    */
   public boolean isProjectMember(User user) {
      return isProjectMember(user, null);
   }

}
