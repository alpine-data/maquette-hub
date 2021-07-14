package maquette.datashop;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.modules.MaquetteModule;
import maquette.core.server.commands.Command;
import maquette.datashop.commands.*;
import maquette.datashop.commands.members.GrantDataAssetMemberCommand;
import maquette.datashop.commands.members.RevokeDataAssetMemberCommand;
import maquette.datashop.commands.requests.*;
import maquette.datashop.ports.DataAssetsRepository;
import maquette.datashop.services.DataAssetServices;
import maquette.datashop.values.DataAssetProviders;

import java.util.Map;

@AllArgsConstructor(staticName = "apply")
public class MaquetteDataShop implements MaquetteModule {

   private final DataAssetsRepository repository;

   private final DataAssetProviders providers;

   private final DataAssetServices services;

   public static MaquetteDataShop apply(DataAssetsRepository repository) {
      return apply(repository, null, null);
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
