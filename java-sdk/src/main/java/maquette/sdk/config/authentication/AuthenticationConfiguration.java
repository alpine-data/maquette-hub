package maquette.sdk.config.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = DataAssetKeyAuthentication.class, name = DataAssetKeyAuthentication.TYPE_KEY),
      @JsonSubTypes.Type(value = ProjectKeyAuthentication.class, name = ProjectKeyAuthentication.TYPE_KEY),
      @JsonSubTypes.Type(value = StupidAuthentication.class, name = StupidAuthentication.TYPE_KEY),
   })
public interface AuthenticationConfiguration {

   @JsonIgnore
   String getTypeKey();

   default AuthenticationConfiguration withEnvironmentOverrides() {
      var result = this;
      var envType = System.getenv("AUTH_TYPE");

      if (!Objects.isNull(envType)) {
         switch (envType) {
            case DataAssetKeyAuthentication.TYPE_KEY:
               result = DataAssetKeyAuthentication.apply("", "");
               break;
            case ProjectKeyAuthentication.TYPE_KEY:
               result = ProjectKeyAuthentication.apply("", "");
               break;
            case StupidAuthentication.TYPE_KEY:
               result = StupidAuthentication.apply();
               break;
         }
      }

      if (result instanceof DataAssetKeyAuthentication) {
         var envKey = System.getenv("MQ_AUTH_KEY");
         var envSecret = System.getenv("MQ_AUTH_SECRET");

         if (!Objects.isNull(envKey)) {
            result = ((DataAssetKeyAuthentication) result).withKey(envKey);
         }

         if (!Objects.isNull(envSecret)) {
            result = ((DataAssetKeyAuthentication) result).withSecret(envSecret);
         }
      }

      if (result instanceof ProjectKeyAuthentication) {
         var envKey = System.getenv("MQ_AUTH_KEY");
         var envSecret = System.getenv("MQ_AUTH_SECRET");

         if (!Objects.isNull(envKey)) {
            result = ((ProjectKeyAuthentication) result).withKey(envKey);
         }

         if (!Objects.isNull(envSecret)) {
            result = ((ProjectKeyAuthentication) result).withSecret(envSecret);
         }
      }

      if (result instanceof StupidAuthentication) {
         var envUsername = System.getenv("MQ_AUTH_USERNAME");
         var envRoles = System.getenv("MQ_AUTH_ROLES");

         if (!Objects.isNull(envUsername)) {
            result = ((StupidAuthentication) result).withUsername(envUsername);
         }

         if (!Objects.isNull(envRoles)) {
            var roles = Arrays.stream(envRoles.split("/")).filter(s -> !s.isBlank()).collect(Collectors.toList());
            result = ((StupidAuthentication) result).withRoles(roles);
         }
      }

      return result;
   }

}
