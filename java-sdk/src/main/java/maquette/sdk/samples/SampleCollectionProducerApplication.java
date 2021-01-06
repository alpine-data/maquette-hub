package maquette.sdk.samples;

import akka.actor.ActorSystem;
import akka.stream.alpakka.file.javadsl.Directory;
import maquette.sdk.dsl.Maquette;

import java.io.File;

public class SampleCollectionProducerApplication {

   public static void main(String... args) {
      var system = ActorSystem.apply();
      var directory = (new File("/Users/michaelwellner/Downloads/sample-collection")).toPath();
      var collection = Maquette.apply().collections("some-collection");

      Directory
         .walk(directory)
         .runWith(collection.createPathSink("some update", directory), system)
         .thenCompose(i -> {
            System.out.println("Tagging ....");
            return collection.tag("some-tag", "some message");
         })
         .thenRun(system::terminate);
   }

}
