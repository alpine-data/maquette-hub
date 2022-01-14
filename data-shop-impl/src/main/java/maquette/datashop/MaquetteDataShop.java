package maquette.datashop;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.commands.Command;
import maquette.datashop.commands.*;
import maquette.datashop.commands.members.GrantDataAssetMemberCommand;
import maquette.datashop.commands.members.RevokeDataAssetMemberCommand;
import maquette.datashop.commands.requests.*;
import maquette.datashop.configuration.DataShopConfiguration;
import maquette.datashop.databind.MaquetteDataShopObjectMapperFactory;
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.providers.DataAssetProviders;
import maquette.datashop.services.DataAssetServices;
import maquette.datashop.services.DataAssetServicesFactory;
import maquette.datashop.ports.WorkspacesServicePort;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteDataShop implements MaquetteModule {

    public static final String MODULE_NAME = "data-shop";

    private final DataAssetServices services;

    private final DataAssetProviders providers;

    private final DataAssetEntities entities;

    private final DataShopConfiguration configuration;

    public static MaquetteDataShop apply(DataAssetsRepository repository, WorkspacesServicePort workspaces, DataAssetProvider... dataAssetProviders) {
        var configuration = DataShopConfiguration.apply();
        var providers = DataAssetProviders.apply(dataAssetProviders);
        var entities = DataAssetEntities.apply(repository, providers);
        var services = DataAssetServicesFactory.apply(configuration, entities, workspaces, providers);

        return apply(services, providers, entities, configuration);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public Map<String, Class<? extends Command>> getCommands() {
        var commands = Maps.<String, Class<? extends Command>>newHashMap();

        commands.put("data-assets grant", GrantDataAssetMemberCommand.class);
        commands.put("data-assets revoke", RevokeDataAssetMemberCommand.class);

        commands.put("data-assets request approve", ApproveAccessRequestCommand.class);
        commands.put("data-assets request create", CreateAccessRequestCommand.class);
        commands.put("data-assets request create view", CreateAccessRequestViewCommand.class);
        commands.put("data-assets request get", GetAccessRequestCommand.class);
        commands.put("data-assets request grant", GrantAccessRequestCommand.class);
        commands.put("data-assets request reject", RejectAccessRequestCommand.class);
        commands.put("data-assets request update", UpdateAccessRequestCommand.class);
        commands.put("data-assets request withdraw", WithdrawAccessRequestCommand.class);

        commands.put("data-assets approve", ApproveDataAssetCommand.class);
        commands.put("data-assets create", CreateDataAssetCommand.class);
        commands.put("data-assets decline", DeclineDataAssetCommand.class);
        commands.put("data-assets deprecate", DeprecateDataAssetCommand.class);
        commands.put("data-assets get", GetDataAssetCommand.class);
        commands.put("data-assets view", DataAssetViewCommand.class);
        commands.put("data-assets list", ListDataAssetsCommand.class);
        commands.put("data-assets query", QueryDataAssetsCommand.class);
        commands.put("data-assets remove", RemoveDataAssetCommand.class);
        commands.put("data-assets request-review", RequestReviewDataAssetCommand.class);
        commands.put("data-assets update-custom-settings", UpdateCustomDataAssetSettingsCommand.class);
        commands.put("data-assets update", UpdateDataAssetCommand.class);

        this.providers.toMap().values().forEach(provider -> commands.putAll(provider.getCustomCommands()));

        return commands;
    }

    public DataShopConfiguration getConfiguration() {
        return configuration;
    }

    public DataAssetEntities getEntities() {
        return entities;
    }

    public DataAssetServices getServices() {
        return services;
    }

    public DataAssetProviders getProviders() {
        return providers;
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        runtime.withObjectMapperFactory(MaquetteDataShopObjectMapperFactory.apply(runtime.getObjectMapperFactory()));

        for (var provider : this.getProviders().toMap().values()) {
            provider.configure(runtime);
        }

        MaquetteModule.super.start(runtime);
    }

}
