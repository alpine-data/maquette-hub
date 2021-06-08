package samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import maquette.sdk.dsl.Maquette;

public class SampleDatasetConsumerApplication {

    public static void main(String ...args) {
        var system = ActorSystem.apply();
        var dataset = "some-dataset";
        var countries = Maquette
           .create()
           .dataset(dataset)
           .source(Country.class);

        countries
            .map(c -> {
                System.out.println(c);
                return c;
            })
            .runWith(Sink.ignore(), system)
            .thenRun(system::terminate);
    }

}
