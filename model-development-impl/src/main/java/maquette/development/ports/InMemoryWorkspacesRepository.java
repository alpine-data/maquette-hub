package maquette.development.ports;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.ports.InMemoryMembersRepository;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.development.values.WorkspaceMemberRole;
import maquette.development.values.WorkspaceProperties;
import maquette.development.values.sandboxes.Sandbox;
import maquette.development.values.stacks.StackProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public class InMemoryWorkspacesRepository implements WorkspacesRepository {

    private final InMemoryMembersRepository<WorkspaceMemberRole> members;

    private final Map<UID, WorkspacePersisted> store;

    public static InMemoryWorkspacesRepository apply() {
        var members = InMemoryMembersRepository.<WorkspaceMemberRole>apply();
        var store = Maps.<UID, WorkspacePersisted>newHashMap();
        return apply(members, store);
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<WorkspaceMemberRole>>> findAllMembers(UID parent) {
        return members.findAllMembers(parent);
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<WorkspaceMemberRole>>> findMembersByRole(UID parent,
                                                                                              WorkspaceMemberRole role) {
        return members.findMembersByRole(parent, role);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateMember(UID parent,
                                                      GrantedAuthorization<WorkspaceMemberRole> member) {
        return members.insertOrUpdateMember(parent, member);
    }

    @Override
    public CompletionStage<Done> removeMember(UID parent,
                                              Authorization member) {
        return members.removeMember(parent, member);
    }

    @Override
    public CompletionStage<Optional<WorkspaceProperties>> findWorkspaceById(UID id) {
        var result = store
            .values()
            .stream()
            .map(p -> p.properties)
            .filter(p -> p.getId().equals(id))
            .findFirst();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<WorkspaceProperties>> findWorkspaceByName(String name) {
        var result = store
            .values()
            .stream()
            .map(p -> p.properties)
            .filter(p -> p.getName().equals(name))
            .findFirst();
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateWorkspace(WorkspaceProperties updated) {
        if (store.containsKey(updated.getId())) {
            store.put(updated.getId(), store.get(updated.getId()).withProperties(updated));
        } else {
            store.put(updated.getId(), WorkspacePersisted.apply(updated));
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Stream<WorkspaceProperties>> getWorkspaces() {
        return null;
    }

    @Override
    public CompletionStage<Done> removeWorkspace(UID project) {
        return null;
    }

    @With
    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class WorkspacePersisted {

        WorkspaceProperties properties;

        List<GrantedAuthorization<WorkspaceMemberRole>> members;

        List<Sandbox> sandboxes;

        List<StackProperties> stackProperties;

        String id;

        static WorkspacePersisted apply(WorkspaceProperties properties) {
            return apply(properties, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(),
                properties.getId()
                    .getValue());
        }
    }
}
