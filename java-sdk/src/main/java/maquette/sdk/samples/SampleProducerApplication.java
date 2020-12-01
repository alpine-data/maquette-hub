package maquette.sdk.samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.DatasetProducerFactory;
import maquette.sdk.dsl.MaquetteConfiguration;

import java.util.concurrent.ExecutionException;

public class SampleProducerApplication {

    public static void main(String ...args) throws ExecutionException, InterruptedException {
        final MaquetteConfiguration config = MaquetteConfiguration
            .apply()
            .withBaseUrl("http://localhost:9042")
            .withUser("alice")
            .withToken(null);

        final DatasetProducerFactory dsf = DatasetProducerFactory
            .apply() // initialize with defaults
            .withMaquette(config);

        final String dataset = "some-dataset";

        final ActorSystem system = ActorSystem.apply("sample");

        Source
            .range(1,100)
            .mapConcat(i -> Country.getSample())
            .runWith(dsf.createSink(dataset, Country.class), system)
            .thenRun(system::terminate)
            .toCompletableFuture()
            .get();
    }

}
