package maquette.sdk.samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import lombok.Data;
import maquette.sdk.dsl.Maquette;
import maquette.sdk.dsl.MaquetteConfiguration;

public class SampleDataSourceConsumer {

   @Data
   public static class User {

      int id;

      String name;

   }

   public static void main(String... args) {
      var system = ActorSystem.apply();
      var config = MaquetteConfiguration
         .apply()
         .withBaseUrl("http://localhost:9042")
         .withUser("alice")
         .withToken(null);

      Maquette
         .apply(config)
         .source("users")
         .createSource(User.class)
         .map(c -> {
            System.out.println(c);
            return c;
         })
         .runWith(Sink.ignore(), system)
         .thenRun(system::terminate);
   }

}
