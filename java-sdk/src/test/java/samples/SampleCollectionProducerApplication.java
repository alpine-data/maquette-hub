package samples;

import akka.actor.ActorSystem;
import maquette.sdk.dsl.Maquette;

import java.io.File;

public class SampleCollectionProducerApplication {

   public static void main(String... args) {
      var directory = (new File("/Users/michaelwellner/Downloads/sample-collection")).toPath();

      Maquette
         .create()
         .collections("some-dataset")
         .writeDirectory(directory, "some message");
   }

}
