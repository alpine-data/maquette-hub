package maquette.sdk.samples;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.DatasetConsumerFactory;
import maquette.sdk.dsl.MaquetteConfiguration;

public class SampleConsumerApplication {

    public static void main(String ...args) {
        final MaquetteConfiguration config = MaquetteConfiguration
            .apply()
            .withBaseUrl("http://localhost:9042")
            .withUser("alice")
            .withToken(null);

        final DatasetConsumerFactory dcf = DatasetConsumerFactory
            .apply()
            .withMaquette(config);

        final String dataset = "some-dataset";

        final ActorSystem system = ActorSystem.create();
        Source<Country, NotUsed> countries = dcf.createSource(dataset, "1.0.0", Country.class);

        countries
            .map(c -> {
                System.out.println(c);
                return c;
            })
            .runWith(Sink.ignore(), system)
            .thenRun(system::terminate);
    }

}
