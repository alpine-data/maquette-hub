package samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.Maquette;

import java.util.concurrent.ExecutionException;

public class SampleDatasetProducerApplication {

    public static void main(String ...args) throws ExecutionException, InterruptedException {
        final ActorSystem system = ActorSystem.apply("sample");

        var sink = Maquette
           .create()
           .dataset("test-dataset-1")
           .sink(Country.class, "some message");

        Source
            .range(1,100)
            .mapConcat(i -> Country.getSample())
            .runWith(sink, system)
            .thenRun(system::terminate)
            .toCompletableFuture()
            .get();
    }

}
