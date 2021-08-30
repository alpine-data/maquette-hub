package maquette;

import maquette.core.Maquette;
import maquette.core.MaquetteRuntime;
import maquette.datashop.MaquetteDataShop;
import maquette.datashop.ports.InMemoryDataAssetsRepository;

/**
 * This object ensembles Maquette Community Edition.
 */
public class Application {

   public static void main(String[] args) {
        Maquette
           .apply()
           .configure(Application::configure)
           .start();
   }

   private static MaquetteRuntime configure(MaquetteRuntime runtime) {
      var dataAssetsRepository = InMemoryDataAssetsRepository.apply();

      return runtime
         .withModule(rt -> MaquetteDataShop.apply(runtime, dataAssetsRepository));
   }

}
