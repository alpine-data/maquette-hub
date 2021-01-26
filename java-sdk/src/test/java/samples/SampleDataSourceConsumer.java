package samples;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Sink;
import lombok.Data;
import maquette.sdk.dsl.Maquette;

public class SampleDataSourceConsumer {

   @Data
   public static class User {

      int id;

      String name;

   }

   @Data
   public static class Store {

      int store;

      String city;

      String state;

      String country;

   }

   public static void main(String... args) {
      var system = ActorSystem.apply();

      Maquette
         .create()
         .source("users")
         .source(Store.class)
         .map(c -> {
            System.out.println(c);
            return c;
         })
         .runWith(Sink.ignore(), system)
         .thenRun(system::terminate);
   }

}