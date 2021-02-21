package maquette.core.entities.projects.ports;

import akka.Done;
import maquette.core.entities.projects.model.model.ModelProperties;
import maquette.core.entities.projects.model.model.ModelMemberRole;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ModelsRepository {

   CompletionStage<Done> insertOrUpdateModel(UID project, ModelProperties model);

   CompletionStage<Optional<ModelProperties>> findModelByName(UID project, String name);

   CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findAllMembers(UID project, String model);

   CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findMembersByRole(UID project, String model, ModelMemberRole role);

   CompletionStage<Done> insertOrUpdateMember(UID project, String model, GrantedAuthorization<ModelMemberRole> member);

   CompletionStage<Done> removeMember(UID project, String model, Authorization member);

}
