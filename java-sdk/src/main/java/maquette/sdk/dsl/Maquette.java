package maquette.sdk.dsl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Maquette {

   private final MaquetteConfiguration config;

   public static Maquette apply(MaquetteConfiguration config) {
      return new Maquette(config);
   }

   public static Maquette apply() {
      var config = MaquetteConfiguration.apply();
      return apply(config);
   }

   public Collection collections(String name) {
      return Collection.apply(name, config);
   }

   public Dataset datasets(String name) {
      return Dataset.apply(name, config);
   }

   public DataSource source(String name) {
      return DataSource.apply(name, config);
   }

}
