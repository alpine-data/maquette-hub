package maquette.sdk.dsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.ObjectMapperFactory;
import maquette.sdk.config.MaquetteConfiguration;
import okhttp3.OkHttpClient;

import java.util.function.Function;

@AllArgsConstructor(staticName = "create", access = AccessLevel.PRIVATE)
public final class Maquette {

   private final MaquetteConfiguration config;

   private final OkHttpClient client;

   private final ObjectMapper om;

   public static Maquette create(MaquetteConfiguration config) {
      return new Maquette(config, new OkHttpClient(), ObjectMapperFactory.apply().createJson(true));
   }

   public static Maquette create() {
      var config = MaquetteConfiguration.apply();
      return create(config);
   }

   public Collection collection(String name) {
      return Collection.apply(name, MaquetteClient.apply(client, om, config));
   }

   public Dataset dataset(String name) {
      return Dataset.apply(name, MaquetteClient.apply(client, om, config));
   }

   public DataSource source(String name) {
      return DataSource.apply(name, MaquetteClient.apply(client, om, config));
   }

   public Maquette withClient(OkHttpClient client) {
      return create(config, client, om);
   }

   public Maquette withConfiguration(MaquetteConfiguration configuration) {
      return create(configuration, client, om);
   }

   public Maquette withObjectMapper(ObjectMapper om) {
      return create(config, client, om);
   }

   public Maquette updateConfiguration(Function<MaquetteConfiguration, MaquetteConfiguration> updater) {
      return withConfiguration(updater.apply(config));
   }

}
