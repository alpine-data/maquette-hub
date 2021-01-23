package samples;

import maquette.sdk.config.MaquetteConfiguration;

public class SampleCollectionProducerApplication {

   public static void main(String... args) {
      /*
      var system = ActorSystem.apply();
      var directory = (new File("/Users/michaelwellner/Downloads/sample-collection")).toPath();
      var collection = Maquette.apply().collections("some-dataset");

      Directory
         .walk(directory)
         .runWith(collection.createPathSink("some update", directory), system)
         .thenCompose(i -> {
            System.out.println("Tagging ....");
            return collection.tag("some-tag", "some message");
         })
         .thenRun(system::terminate);
       */

      var config = MaquetteConfiguration.apply();
      config.save();
      System.out.println(config);
   }

}
