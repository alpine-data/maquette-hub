package maquette.core.services.data.streams;

import maquette.core.config.RuntimeConfiguration;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class StreamServicesFactory {

   private StreamServicesFactory() {

   }

   public static StreamServices apply(RuntimeConfiguration runtime) {
      var streams = runtime.getStreams();
      var projects = runtime.getProjects();

      var assets = DataAssetServicesFactory.apply(streams, runtime);
      var assetsCompanion = DataAssetCompanion.apply(streams, projects);
      var companion = StreamCompanion.apply(assetsCompanion);
      var delegate = StreamServicesImpl.apply(streams, assets, companion);

      return StreamServicesSecured.apply(delegate, companion, assetsCompanion);
   }

}
