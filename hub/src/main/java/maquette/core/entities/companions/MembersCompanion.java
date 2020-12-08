package maquette.core.entities.companions;

import akka.Done;
import lombok.Getter;
import maquette.core.ports.common.HasMembers;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;
import maquette.core.values.data.DataAssetMemberRole;
import maquette.core.values.exceptions.DomainException;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Getter
public final class MembersCompanion<T extends Enum<T>> {

   private final UID id;

   private final HasMembers<T> repository;

   private MembersCompanion(UID id, HasMembers<T> repository) {
      this.id = id;
      this.repository = repository;
   }

   public static <T extends Enum<T>> MembersCompanion<T> apply(UID id, HasMembers<T> repository) {
      return new MembersCompanion<>(id, repository);
   }

   public CompletionStage<Done> addMember(User executor, Authorization member, T role) {
      if (role.equals(DataAssetMemberRole.OWNER) && !(member instanceof UserAuthorization)) {
         return CompletableFuture.failedFuture(MembersException.invalidOwner());
      }

      var granted = GrantedAuthorization.apply(ActionMetadata.apply(executor), member, role);
      return repository.insertOrUpdateMember(id, granted);
   }

   public CompletionStage<List<GrantedAuthorization<T>>> getMembers() {
      return repository.findAllMembers(id);
   }

   public CompletionStage<Done> removeMember(User executor, Authorization member) {
      if (member instanceof UserAuthorization && executor instanceof AuthenticatedUser && member.getName().equals(((AuthenticatedUser) executor).getId())) {
         return CompletableFuture.failedFuture(MembersException.userCannotRemoveSelf());
      } else {
         return repository.removeMember(id, member);
      }
   }

   public static class MembersException extends RuntimeException implements DomainException {

      private MembersException(String message) {
         super(message);
      }

      public static MembersException userCannotRemoveSelf() {
         var msg = "You cannot revoke your own access.";
         return new MembersException(msg);
      }

      public static MembersException invalidOwner() {
         var msg = "Only users are allowed to be owners of a data asset.";
         return new MembersException(msg);
      }

   }

}
