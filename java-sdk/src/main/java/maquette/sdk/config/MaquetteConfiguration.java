package maquette.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.ObjectMapperFactory;
import maquette.sdk.config.authentication.AuthenticationConfiguration;
import maquette.sdk.config.authentication.StupidAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Value
@AllArgsConstructor(staticName = "apply")
public class MaquetteConfiguration {

   private static final Logger LOG = LoggerFactory.getLogger("maquette.sdk");

   String url;

   ProjectConfiguration project;

   EnvironmentConfiguration environment;

   AuthenticationConfiguration authentication;

   private MaquetteConfiguration() {
      this.url = "http://localhost:3030";
      this.project = ProjectConfiguration.apply();
      this.environment = EnvironmentConfiguration.apply();
      this.authentication = StupidAuthentication.apply();
   }

   public static MaquetteConfiguration apply() {
      var result = new MaquetteConfiguration();
      var file = getDefaultLocation();
      var om = ObjectMapperFactory.apply().createYaml();

      if (Files.exists(file)) {
         try (var is = Files.newInputStream(file)) {
            result = om.readValue(is, MaquetteConfiguration.class);
         } catch (Exception e) {
            LOG.warn("An exception occurred reading the local configuration file `" + file.toString() + "`. Will use default configuration instead.", e);
         }
      } else {
         LOG.info("No local configuration file found at `" + file.toString() + "`. Will use default configuration instead.");
      }

      result = result.withEnvironmentOverrides();

      try {
         var config = om.writeValueAsString(result);
         LOG.info("The following configuration will be used:\n" + config);
      } catch (Exception e) {
         LOG.error("Cannot serialized configuration. This is evil.", e);
      }

      return result;
   }

   public void save() {
      save(getDefaultLocation());
   }

   public void save(Path file) {
      var om = ObjectMapperFactory.apply().createYaml();

      try {
         Files.createDirectories(file.getParent());
         om.writeValue(file.toFile(), this);
      } catch (Exception e) {
         LOG.error("Exception occurred while saving configuration to `" + file + "`.", e);
      }
   }

   public MaquetteConfiguration withEnvironmentOverrides() {
      var result = this;
      var envUrl = System.getenv("MQ_URL");

      if (!Objects.isNull(envUrl)) {
         result = result.withUrl(envUrl);
      }

      result = result.withEnvironment(result.getEnvironment().withEnvironmentOverrides());
      result = result.withProject(result.getProject().withEnvironmentOverrides());
      result = result.withAuthentication(result.getAuthentication().withEnvironmentOverrides());

      return result;
   }

   public MaquetteConfiguration withUrl(String url) {
      return apply(url, project, environment, authentication);
   }

   public MaquetteConfiguration withProject(ProjectConfiguration project) {
      return apply(url, project, environment, authentication);
   }

   public MaquetteConfiguration withEnvironment(EnvironmentConfiguration environment) {
      return apply(url, project, environment, authentication);
   }

   public MaquetteConfiguration withAuthentication(AuthenticationConfiguration authentication) {
      return apply(url, project, environment, authentication);
   }

   private static Path getDefaultLocation() {
      return Paths.get(System.getProperty("user.home")).resolve(".mq").resolve("config.yaml");
   }

}
