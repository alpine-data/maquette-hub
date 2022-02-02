package maquette.datashop.providers.databases;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.providers.databases.commands.AnalyzeDatabaseCommand;
import maquette.datashop.providers.databases.ports.DatabaseDataExplorer;
import maquette.datashop.providers.databases.ports.DatabasePort;
import maquette.datashop.providers.databases.services.DatabaseServices;
import maquette.datashop.providers.databases.services.DatabaseServicesFactory;
import maquette.datashop.services.DataAssetServices;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class Databases implements DataAssetProvider {

    public static final String TYPE_NAME = "database";

    private final DatabaseEntities databases;

    private final WorkspacesServicePort workspaces;

    private MaquetteRuntime runtime;

    public static Databases apply(DatabasePort database, DatabaseDataExplorer explorer, WorkspacesServicePort workspaces) {
        var databases = DatabaseEntities.apply(database, explorer);
        return apply(databases, workspaces, null);
    }

    @Override
    public void configure(MaquetteRuntime runtime) {
        this.runtime = runtime;

        var handlers = DatabasesAPI.apply(this);

        runtime
            .getApp()
            .get("/api/data/databases/:database", handlers.download())
            .get("/api/profiles/databases/:database", handlers.getProfile());
    }

    @Override
    public Map<String, Class<? extends Command>> getCustomCommands() {
        Map<String, Class<? extends Command>> commands = Maps.newHashMap();
        commands.put("databases analyze", AnalyzeDatabaseCommand.class);
        return commands;
    }

    public DataAssetServices getDataAssetServices() {
        if (Objects.isNull(runtime)) {
            throw new IllegalStateException("This method can not be called before everything is initialized.");
        }

        return runtime.getModule(MaquetteDataShop.class).getServices();
    }

    public DatabaseServices getServices() {
        if (Objects.isNull(runtime)) {
            throw new IllegalStateException("This method can not be called before everything is initialized.");
        }

        return DatabaseServicesFactory.apply(runtime, workspaces, databases);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public CompletionStage<Done> onUpdatedCustomSettings(DataAssetEntity entity, Object customSettings) {
        return DataAssetProvider.super.onUpdatedCustomSettings(entity, customSettings);
    }
}
