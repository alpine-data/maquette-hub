package maquette.development.ports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.core.values.UID;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO mw: Add some methods to setup/modify dummy data
@AllArgsConstructor(staticName = "apply")
public final class FakeDataAssetsServicePort implements DataAssetsServicePort {

    private final List<FakeDataAsset> assets;

    private final ObjectMapper om;

    public static FakeDataAssetsServicePort apply() {
        return apply(Lists.newArrayList(), DefaultObjectMapperFactory.apply().createJsonMapper());
    }


    @Override
    public CompletionStage<List<JsonNode>> findDataAccessRequestsByWorkspace(UID workspace) {
        var result = assets
            .stream()
            .flatMap(asset -> asset.accessRequests.stream())
            .filter(req -> req.workspace.equals(workspace))
            .map(req -> om.convertValue(req, JsonNode.class))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<List<JsonNode>> findDataAssetsByWorkspace(UID workspace) {
        var result = assets
            .stream()
            .filter(asset -> asset.accessRequests
                .stream()
                .anyMatch(req -> req.workspace.equals(workspace)))
            .map(asset -> om.convertValue(asset, JsonNode.class))
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<JsonNode> getDataAssetEntity(UID id) {
        return assets
            .stream()
            .filter(asset -> asset.id.equals(id))
            .map(asset -> om.convertValue(asset, JsonNode.class))
            .findFirst()
            .map(CompletableFuture::completedFuture)
            .orElse(CompletableFuture.failedFuture(DataAssetNotFound.apply(id)));
    }

    public static class DataAssetNotFound extends ApplicationException {

        private DataAssetNotFound(String message) {
            super(message);
        }

        public static DataAssetNotFound apply(UID id) {
            var message = String.format("Data asset with id `%s` not found.", id.getValue());
            return new DataAssetNotFound(message);
        }

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class FakeDataAsset {

        /**
         * The unique id of the asset.
         */
        UID id;

        /**
         * The access requests for this asset.
         */
        List<FakeAccessRequest> accessRequests;

        public FakeDataAsset withAccessRequest(FakeAccessRequest request) {
            var updated = Stream
                .concat(
                    accessRequests
                        .stream()
                        .filter(r -> !r.id.equals(request.id)),
                    Stream.of(request))
                .collect(Collectors.toList());

            return FakeDataAsset.apply(id, updated);
        }

    }

    @With
    @Value
    @AllArgsConstructor(staticName = "apply")
    public static class FakeAccessRequest {

        /**
         * The unique id of the request.
         */
        UID id;

        /**
         * Whether the access request is approved or not.
         */
        boolean approved;

        /**
         * The id of the workspace which opened the request.
         */
        UID workspace;

    }

}
