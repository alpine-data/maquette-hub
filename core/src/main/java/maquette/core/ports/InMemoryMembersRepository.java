package maquette.core.ports;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public final class InMemoryMembersRepository<T extends Enum<T>> implements HasMembers<T> {

    private final Map<UID, List<GrantedAuthorization<T>>> store;

    public static <T extends Enum<T>> InMemoryMembersRepository<T> apply() {
        return new InMemoryMembersRepository<>(Maps.newHashMap());
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<T>>> findAllMembers(UID parent) {
        var result = Optional
            .ofNullable(store.get(parent))
            .orElse(List.of());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<T>>> findMembersByRole(UID parent, T role) {
        var result = store
            .getOrDefault(parent, Lists.newArrayList())
            .stream()
            .filter(a -> a
                .getRole()
                .equals(role))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<T> member) {
        var existing = store
            .getOrDefault(parent, Lists.newArrayList())
            .stream()
            .filter(a -> !(a
                .getAuthorization()
                .equals(member.getAuthorization())));

        var updated = Stream
            .concat(existing, Stream.of(member))
            .collect(Collectors.toList());
        store.put(parent, updated);

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> removeMember(UID parent, Authorization member) {
        var updated = store
            .getOrDefault(parent, Lists.newArrayList())
            .stream()
            .filter(a -> !(a
                .getAuthorization()
                .equals(member)))
            .collect(Collectors.toList());

        store.put(parent, updated);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

}
