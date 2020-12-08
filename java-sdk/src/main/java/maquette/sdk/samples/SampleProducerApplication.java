package maquette.sdk.samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;
import maquette.sdk.dsl.Maquette;

import java.util.concurrent.ExecutionException;

public class SampleProducerApplication {

    public static void main(String ...args) throws ExecutionException, InterruptedException {
        final ActorSystem system = ActorSystem.apply("sample");

        Source
            .range(1,100)
            .mapConcat(i -> Country.getSample())
            .runWith(Maquette.apply().datasets("some-dataset").createSink(Country.class), system)
            .thenRun(system::terminate)
            .toCompletableFuture()
            .get();
    }

}
