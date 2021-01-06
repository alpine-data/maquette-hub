package maquette.sdk.samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import maquette.sdk.dsl.Maquette;
import maquette.sdk.dsl.MaquetteConfiguration;

public class SampleDatasetConsumerApplication {

    public static void main(String ...args) {
        var system = ActorSystem.apply();
        var config = MaquetteConfiguration.apply();
        var dataset = "some-dataset";
        var countries = Maquette.apply(config).datasets(dataset).createSource(Country.class);

        countries
            .map(c -> {
                System.out.println(c);
                return c;
            })
            .runWith(Sink.ignore(), system)
            .thenRun(system::terminate);
    }

}
