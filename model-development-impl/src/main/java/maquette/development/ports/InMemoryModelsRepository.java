package maquette.development.ports;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.AllArgsConstructor;
import maquette.core.ports.InMemoryMembersRepository;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.values.model.ModelMemberRole;
import maquette.development.values.model.ModelProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryModelsRepository implements ModelsRepository {

    private final InMemoryMembersRepository<ModelMemberRole> members;

    private List<Pair<UID, ModelProperties>> models;

    public static InMemoryModelsRepository apply() {
        return apply(InMemoryMembersRepository.apply(), Lists.newArrayList());
    }

    @Override
    public CompletionStage<List<ModelProperties>> findAllModelsByWorkspace(UID workspace) {
        return CompletableFuture.completedFuture(
            models
                .stream()
                .filter(m -> m.getKey().equals(workspace))
                .map(Pair::getValue)
                .collect(Collectors.toList())
        );
    }

    @Override
    public CompletionStage<Done> insertOrUpdateModel(UID workspace,
                                                     ModelProperties model) {
        this.models = Streams.concat(
                this.models.stream().filter(
                    m -> !(m.getKey().equals(workspace) && m.getValue().getName().equals(model.getName()))
                ),
                Stream.of(Pair.of(workspace, model))
            )
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Optional<ModelProperties>> findModelByName(UID workspace,
                                                                      String name) {
        return CompletableFuture.completedFuture(
            models
                .stream()
                .filter(m -> m.getKey().equals(workspace) && m.getValue().getName().equals(name))
                .map(Pair::getValue)
                .findFirst()
        );
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findAllMembers(UID workspace,
                                                                                       String model) {
        return this.members.findAllMembers(UID.apply(String.format("%s--%s", workspace, model)));
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<ModelMemberRole>>> findMembersByRole(UID workspace,
                                                                                          String model,
                                                                                          ModelMemberRole role) {
        return this.members.findMembersByRole(UID.apply(String.format("%s--%s", workspace, model)), role);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateMember(UID workspace,
                                                      String model,
                                                      GrantedAuthorization<ModelMemberRole> member) {

        return this.members.insertOrUpdateMember(UID.apply(String.format("%s--%s", workspace, model)), member);
    }

    @Override
    public CompletionStage<Done> removeMember(UID workspace,
                                              String model,
                                              Authorization member) {

        return this.members.removeMember(UID.apply(String.format("%s--%s", workspace, model)), member);
    }
}
