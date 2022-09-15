package maquette.datashop.providers.collections;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.server.commands.Command;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.providers.collections.commands.CreateCollectionTagCommand;
import maquette.datashop.providers.collections.commands.ListCollectionFilesCommand;
import maquette.datashop.providers.collections.model.CollectionDetails;
import maquette.datashop.providers.collections.ports.CollectionsRepository;
import maquette.datashop.providers.collections.services.CollectionServices;
import maquette.datashop.providers.collections.services.CollectionServicesFactory;
import maquette.datashop.values.DataAssetProperties;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;


@AllArgsConstructor(staticName = "apply")
public final class Collections implements DataAssetProvider {
    public static final String TYPE_NAME = "collection";

    private final CollectionsRepository repository;


    private final WorkspacesServicePort workspaces;

    private MaquetteRuntime runtime;

    public static Collections apply(CollectionsRepository repository, WorkspacesServicePort workspaces) {
        return apply(repository, workspaces, null);
    }

    @Override
    public void configure(MaquetteRuntime runtime) {

        this.runtime = runtime;

        var handlers = CollectionsAPI.apply(this);

        runtime
            .getApp()
            .post("/api/data/collections/:collection", handlers.upload())
            .get("/api/data/collections/:collection/latest", handlers.download())
            .get("/api/data/collections/:collection/tags/:tag", handlers.download())
            .get("/api/data/collections/:collection/latest/*", handlers.downloadFile())
            .get("/api/data/collections/:collection/tags/:tag/*", handlers.downloadFile())
            .delete("/api/data/collections/:collection/latest/*", handlers.remove());
    }


    @Override
    public Map<String, Class<? extends Command>> getCustomCommands() {
        Map<String, Class<? extends Command>> commands = Maps.newHashMap();

        commands.put("collections tag", CreateCollectionTagCommand.class);
        commands.put("collections list", ListCollectionFilesCommand.class);

        return commands;
    }


    @Override
    public CompletionStage<?> getDetails(DataAssetProperties properties, Object customSettings) {
        var filesCS = repository.getFiles(properties.getId());
        var tagsCS = repository.findAllTags(properties.getId());

        return Operators.compose(filesCS, tagsCS, CollectionDetails::apply);
    }

    public CollectionServices getServices() {
        if (Objects.isNull(runtime)) {
            throw new IllegalStateException("This method can not be called before everything is initialized.");
        }

        return CollectionServicesFactory.apply(runtime, repository, workspaces);
    }


    @Override
    public String getType() {
        return TYPE_NAME;
    }
}
