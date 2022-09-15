package maquette.datashop.providers.datasets;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.providers.datasets.commands.*;
import maquette.datashop.providers.datasets.ports.DatasetDataExplorer;
import maquette.datashop.providers.datasets.ports.DatasetsRepository;
import maquette.datashop.providers.datasets.services.DatasetServices;
import maquette.datashop.providers.datasets.services.DatasetServicesFactory;
import maquette.datashop.values.DataAssetProperties;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Datasets implements DataAssetProvider {

    public static final String TYPE_NAME = "dataset";

    private final DatasetsRepository repository;

    private final DatasetDataExplorer explorer;

    private final WorkspacesServicePort workspaces;

    private MaquetteRuntime runtime;

    public static Datasets apply(DatasetsRepository repository, DatasetDataExplorer explorer,
                                 WorkspacesServicePort workspaces) {
        return apply(repository, explorer, workspaces, null);
    }

    @Override
    public void configure(MaquetteRuntime runtime) {
        this.runtime = runtime;

        var handlers = DatasetsAPI.apply(this);

        runtime
            .getApp()
            .post("/api/data/datasets/:dataset", handlers.uploadDatasetFile())
            .post("/api/data/datasets/:dataset/:revision", handlers.upload())
            .get("/api/data/datasets/:dataset", handlers.downloadLatestDatasetVersion())
            .get("/api/data/datasets/:dataset/:version", handlers.downloadDatasetVersion())
            .get("/api/profiles/datasets/:dataset/:version", handlers.getProfile());
    }

    @Override
    public Map<String, Class<? extends Command>> getCustomCommands() {
        Map<String, Class<? extends Command>> commands = Maps.newHashMap();
        commands.put("datasets revisions commit", CommitRevisionCommand.class);
        commands.put("datasets revisions create", CreateRevisionCommand.class);
        commands.put("datasets versions", ListVersionsCommand.class);
        commands.put("datasets versions analyze", AnalyzeVersionCommand.class);
        commands.put("datasets versions get", GetVersionCommand.class);
        return commands;
    }

    @Override
    public CompletionStage<?> getDetails(DataAssetProperties properties, Object customSettings) {
        return repository.findAllVersions(properties.getId());
    }

    public DatasetServices getServices() {
        if (Objects.isNull(runtime)) {
            throw new IllegalStateException("This method can not be called before everything is initialized.");
        }

        return DatasetServicesFactory.apply(runtime, repository, explorer, workspaces);
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }


}
