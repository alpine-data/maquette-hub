package samples;

import maquette.sdk.dsl.Maquette;

import java.io.File;

public class SampleCollectionProducerApplication {

   public static void main(String... args) {
      var directory = (new File("/Users/michaelwellner/Downloads/mnist_png")).toPath();

      /*
      Maquette
         .create()
         .collection("mnist-image-classsification")
         .writeDirectory(directory, "Extracted from the internet and transformed to PNG.");
      */

      Maquette
         .create()
         .collection("mnist-image-classsification")
         .saveFiles(new File("/Users/michaelwellner/Downloads/minst-collection").toPath());
   }

}
