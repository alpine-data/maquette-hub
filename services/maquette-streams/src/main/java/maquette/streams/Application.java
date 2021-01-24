package maquette.streams;

import maquette.streams.adapters.RecordsRepositoryAdapter;
import maquette.streams.adapters.TopicsRepositoryAdapter;
import maquette.streams.common.databind.ObjectMapperFactory;
import maquette.streams.core.CoreApplication;
import maquette.streams.core.config.ApplicationConfiguration;

public class Application {

   public static void main(String ...args) {
      var om = ObjectMapperFactory.apply().create(true);
      var config = ApplicationConfiguration.apply();
      var topics = TopicsRepositoryAdapter.apply(om);
      var records = RecordsRepositoryAdapter.apply(om);

      CoreApplication.apply(config, topics, records).run();
   }

}
