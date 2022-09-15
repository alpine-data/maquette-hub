package maquette.datashop.providers.datasets.ports;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.datashop.providers.datasets.model.CommittedRevision;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.model.Revision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryDatasetsRepository implements DatasetsRepository {

    private final Map<UID, Map<UID, Revision>> revisions;

    private final RecordsStore records;

    public static InMemoryDatasetsRepository apply() {
        return apply(Maps.newHashMap(), InMemoryRecordsStore.apply());
    }

    @Override
    public CompletionStage<List<Revision>> findAllRevisions(UID dataset) {
        var result = new ArrayList<>(revisions
            .getOrDefault(dataset, Maps.newHashMap())
            .values());
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<List<CommittedRevision>> findAllVersions(UID dataset) {
        var result = revisions
            .getOrDefault(dataset, Maps.newHashMap())
            .values()
            .stream()
            .filter(r -> r
                .getCommit()
                .isPresent())
            .map(r -> r
                .getCommit()
                .get())
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<Revision>> findRevisionById(UID dataset, UID revision) {
        var result = Optional.ofNullable(revisions
            .getOrDefault(dataset, Maps.newHashMap())
            .getOrDefault(revision, null));

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<CommittedRevision>> findRevisionByVersion(UID dataset, DatasetVersion version) {
        var result = revisions
            .getOrDefault(dataset, Maps.newHashMap())
            .values()
            .stream()
            .filter(r -> r
                .getCommit()
                .isPresent())
            .map(r -> r
                .getCommit()
                .get())
            .filter(r -> r
                .getVersion()
                .equals(version))
            .findFirst();

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateRevision(UID dataset, Revision revision) {
        if (!revisions.containsKey(dataset)) {
            revisions.put(dataset, Maps.newHashMap());
        }

        revisions
            .get(dataset)
            .put(revision.getId(), revision);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public RecordsStore getRecordsStore(UID dataset) {
        return records;
    }

}
