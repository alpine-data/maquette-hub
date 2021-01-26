package samples;

import maquette.sdk.dsl.Maquette;

import java.io.File;

public class SampleCollectionProducerApplication {

   public static void main(String... args) {
      var directory = (new File("/Users/michaelwellner/Downloads/sample-collection")).toPath();

      Maquette
         .create()
         .collection("some-dataset")
         .writeDirectory(directory, "some message");
   }

}
