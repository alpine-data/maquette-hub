package maquette.core.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Abstract interface for ports/ resources with member management.
 * @param <T> An enumeration type representing possible roles.
 */
public interface HasMembers<T extends Enum<T>> {

   /**
    * Retrieve all members for a parent.
    *
    * @param parent The id of the parent resource.
    * @return The list of members.
    */
   CompletionStage<List<GrantedAuthorization<T>>> findAllMembers(UID parent);

   /**
    * Retrieve all members with a given role.
    *
    * @param parent The id of the parent resource.
    * @param role The role the member should have.
    * @return The list of members with the provided role.
    */
   CompletionStage<List<GrantedAuthorization<T>>> findMembersByRole(UID parent, T role);

   /**
    * Add member to the resource.
    *
    * @param parent The id of the parent resource.
    * @param member The member/ authorization to be added.
    * @return Done.
    */
   CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<T> member);

   /**
    * Remove an authorization from a resource.
    *
    * @param parent The id of the parent resource.
    * @param member The member/ authorization to be removed.
    * @return Done.
    */
   CompletionStage<Done> removeMember(UID parent, Authorization member);

}
