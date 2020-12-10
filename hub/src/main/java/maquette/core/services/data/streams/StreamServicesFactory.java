package maquette.core.services.data.streams;

import maquette.core.entities.data.streams.StreamEntities;
import maquette.core.entities.projects.ProjectEntities;
import maquette.core.services.data.DataAssetCompanion;
import maquette.core.services.data.DataAssetServicesFactory;

public final class StreamServicesFactory {

   private StreamServicesFactory() {

   }

   public static StreamServices apply(StreamEntities streams, ProjectEntities projects) {
      var assets = DataAssetServicesFactory.apply(streams, projects);
      var assetsCompanion = DataAssetCompanion.apply(streams, projects);
      var companion = StreamCompanion.apply(assetsCompanion);
      var delegate = StreamServicesImpl.apply(streams, assets, companion);

      return StreamServicesSecured.apply(delegate, companion, assetsCompanion);
   }

}
