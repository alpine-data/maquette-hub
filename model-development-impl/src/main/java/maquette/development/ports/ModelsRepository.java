package maquette.development.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ModelsRepository {

    CompletionStage<Done> insertOrUpdateModel(UID workspace, ModelProperties model);

    CompletionStage<Optional<ModelProperties>> findModelByName(UID workspace, String name);

    CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findAllMembers(UID workspace, String model);

    CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findMembersByRole(UID workspace, String model,
                                                                                   ModelMemberRole role);

    CompletionStage<Done> insertOrUpdateMember(UID workspace, String model,
                                               GrantedAuthorization<ModelMemberRole> member);

    CompletionStage<Done> removeMember(UID workspace, String model, Authorization member);

}
