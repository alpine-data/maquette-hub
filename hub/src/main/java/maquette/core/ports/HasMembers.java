package maquette.core.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.authorization.UserAuthorization;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface HasMembers<T extends Enum<T>> {

   CompletionStage<List<GrantedAuthorization<T>>> findAllMembers(UID parent);

   CompletionStage<List<GrantedAuthorization<T>>> findMembersByRole(UID parent, T role);

   CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<T> member);

   CompletionStage<Done> removeMember(UID parent, Authorization member);

}
