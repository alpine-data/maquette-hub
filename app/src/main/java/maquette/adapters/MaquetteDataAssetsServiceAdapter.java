package maquette.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.values.UID;
import maquette.datashop.MaquetteDataShop;
import maquette.development.ports.DataAssetsServicePort;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaquetteDataAssetsServiceAdapter implements DataAssetsServicePort {

    private final ObjectMapper om;

    private MaquetteDataShop module;

    public static MaquetteDataAssetsServiceAdapter apply(ObjectMapper om) {
        return new MaquetteDataAssetsServiceAdapter(om, null);
    }

    @Override
    public CompletionStage<List<JsonNode>> findDataAccessRequestsByWorkspace(UID workspace) {
        if (Objects.isNull(module)) {
            throw NotInitializedException.apply(MaquetteDataShop.MODULE_NAME);
        }

        return module
            .getEntities()
            .getDataAccessRequestsByWorkspace(workspace)
            .thenApply(requests -> requests
                .stream()
                .map(request -> om.convertValue(request, JsonNode.class))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<List<JsonNode>> findDataAssetsByWorkspace(UID workspace) {
        if (Objects.isNull(module)) {
            throw NotInitializedException.apply(MaquetteDataShop.MODULE_NAME);
        }


        return module
            .getEntities()
            .getDataAssetsByWorkspace(workspace)
            .thenApply(assets -> assets
                .stream()
                .map(asset -> om.convertValue(asset, JsonNode.class))
                .collect(Collectors.toList()));
    }

    @Override
    public CompletionStage<JsonNode> getDataAssetEntity(UID id) {
        if (Objects.isNull(module)) {
            throw NotInitializedException.apply(MaquetteDataShop.MODULE_NAME);
        }

        return module
            .getEntities()
            .getById(id)
            .getProperties()
            .thenApply(asset -> om.convertValue(asset, JsonNode.class));
    }

    public void setMaquetteModule(MaquetteDataShop module) {
        this.module = module;
    }

}
