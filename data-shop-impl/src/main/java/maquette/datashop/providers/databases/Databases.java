package maquette.datashop.providers.databases;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.core.values.user.User;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.ports.WorkspacesServicePort;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.providers.DataAssetSettings;
import maquette.datashop.providers.databases.commands.AnalyzeDatabaseCommand;
import maquette.datashop.providers.databases.commands.GetDatabaseConnectionCommand;
import maquette.datashop.providers.databases.commands.TestDatabaseConnectionCommand;
import maquette.datashop.providers.databases.model.DatabaseSettings;
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

    public static Databases apply(DatabasePort database, DatabaseDataExplorer explorer,
                                  WorkspacesServicePort workspaces) {
        var databases = DatabaseEntities.apply(database, explorer);
        return apply(databases, workspaces, null);
    }

    @Override
    public void configure(MaquetteRuntime runtime) {
        this.runtime = runtime;

        var handlers = DatabasesAPI.apply(this);

        runtime
            .getApp()
            .get("/api/data/databases/:database/:query", handlers.download())
            .post("/api/data/databases/:database/custom", handlers.downloadCustomQuery())
            .get("/api/profiles/databases/:database", handlers.getProfile());
    }

    @Override
    public Map<String, Class<? extends Command>> getCustomCommands() {
        Map<String, Class<? extends Command>> commands = Maps.newHashMap();
        commands.put("databases analyze", AnalyzeDatabaseCommand.class);
        commands.put("databases test", TestDatabaseConnectionCommand.class);
        commands.put("databases session", GetDatabaseConnectionCommand.class);
        return commands;
    }

    public DataAssetServices getDataAssetServices() {
        if (Objects.isNull(runtime)) {
            throw new IllegalStateException("This method can not be called before everything is initialized.");
        }

        return runtime.getModule(MaquetteDataShop.class).getServices();
    }

    @Override
    public Class<? extends DataAssetSettings> getSettingsType() {
        return DatabaseSettings.class;
    }

    public DatabaseServices getServices() {
        if (Objects.isNull(runtime)) {
            throw new IllegalStateException("This method can not be called before everything is initialized.");
        }

        return DatabaseServicesFactory.apply(runtime, workspaces, databases);
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }

    @Override
    public CompletionStage<Done> onCreated(User executor, DataAssetEntity entity, Object customSettings) {
        return entity
            .getProperties()
            .thenApply(properties -> properties.getMetadata().getName())
            .thenCompose(database -> getServices().analyze(executor, database));
    }

    @Override
    public CompletionStage<Done> onUpdatedCustomSettings(User executor, DataAssetEntity entity, Object customSettings) {
        return entity
            .getCustomSettings(DatabaseSettings.class)
            .thenCompose(settings -> onCreated(executor, entity, settings));
    }

}
