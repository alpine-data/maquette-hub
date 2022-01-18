package maquette.development.ports;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryModelsRepository implements ModelsRepository {

    @Override
    public CompletionStage<Done> insertOrUpdateModel(UID workspace,
                                                     ModelProperties model) {
        return null;
    }

    @Override
    public CompletionStage<Optional<ModelProperties>> findModelByName(UID workspace,
                                                                      String name) {
        return null;
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findAllMembers(UID workspace,
                                                                                       String model) {
        return null;
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findMembersByRole(UID workspace,
                                                                                          String model,
                                                                                          ModelMemberRole role) {
        return null;
    }

    @Override
    public CompletionStage<Done> insertOrUpdateMember(UID workspace,
                                                      String model,
                                                      GrantedAuthorization<ModelMemberRole> member) {
        return null;
    }

    @Override
    public CompletionStage<Done> removeMember(UID workspace,
                                              String model,
                                              Authorization member) {
        return null;
    }
}
