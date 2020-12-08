package maquette.sdk.dsl;

import akka.Done;
import akka.stream.javadsl.Sink;
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class Dataset {

   String name;

   MaquetteConfiguration config;

   public <T> Sink<T, CompletionStage<Done>> createSink(Class<T> cls) {
      final DatasetProducerFactory dsf = DatasetProducerFactory
         .apply() // initialize with defaults
         .withMaquette(config);

      return dsf.createSink(name, cls);
   }

}
