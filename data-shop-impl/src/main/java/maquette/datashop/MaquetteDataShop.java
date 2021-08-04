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
import maquette.datashop.entities.DataAssetEntities;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.services.DataAssetServices;
import maquette.datashop.services.DataAssetServicesFactory;
import maquette.datashop.values.providers.DataAssetProvider;
import maquette.datashop.values.providers.DataAssetProviders;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public class MaquetteDataShop implements MaquetteModule {

   private final DataAssetServices services;

   private final DataAssetProviders providers;

   public static MaquetteDataShop apply(MaquetteRuntime runtime, DataAssetsRepository repository, DataAssetProvider... dataAssetProviders) {
      var providers = DataAssetProviders.apply(dataAssetProviders);
      var entities = DataAssetEntities.apply(repository, providers);
      var services = DataAssetServicesFactory.apply(runtime, entities, null);

      return apply(services, providers);
   }

   @Override
   public String getName() {
      return "data-shop";
   }

   @Override
   public Map<String, Class<? extends Command>> getCommands() {
      var commands = Maps.<String, Class<? extends Command>>newHashMap();

      commands.put("data-assets grant", GrantDataAssetMemberCommand.class);
      commands.put("data-assets revoke", RevokeDataAssetMemberCommand.class);

      commands.put("data-assets request create", CreateAccessRequestCommand.class);
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
      commands.put("data-assets list", ListDataAssetsCommand.class);
      commands.put("data-assets remove", RemoveDataAssetCommand.class);
      commands.put("data-assets request-review", RequestReviewDataAssetCommand.class);
      commands.put("data-assets update-custom-settings", UpdateCustomDataAssetSettingsCommand.class);
      commands.put("data-assets update", UpdateDataAssetCommand.class);

      return commands;
   }

   public DataAssetServices getServices() {
      return services;
   }

   public DataAssetProviders getProviders() {
      return providers;
   }

}
