package maquette.datashop.providers;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.server.commands.Command;
import maquette.datashop.entities.DataAssetEntity;
import maquette.datashop.providers.DataAssetProvider;
import maquette.datashop.values.DataAssetProperties;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A fake data asset provider implementation w/o any function.
 */
@AllArgsConstructor(staticName = "apply")
public final class FakeProvider implements DataAssetProvider {

   public static final String NAME = "fake";

   @Override
   public String getType() {
      return NAME;
   }

}
