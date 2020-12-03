package maquette.sdk.dsl;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final  class Maquette {

   private static final MaquetteConfiguration config = MaquetteConfiguration
      .apply()
      .withBaseUrl("http://localhost:9042")
            .withUser("alice")
            .withToken(null);

   public Dataset datasets(String name) {
      return Dataset.apply(name, config);
   }

   public <T> void put(List<T> data) {

   }

}
