package maquette.sdk.dsl;

import akka.japi.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import maquette.common.Operators;
import maquette.sdk.commands.Command;
import maquette.sdk.config.MaquetteConfiguration;
import maquette.sdk.config.authentication.DataAssetKeyAuthentication;
import maquette.sdk.config.authentication.ProjectKeyAuthentication;
import maquette.sdk.config.authentication.StupidAuthentication;
import maquette.sdk.model.exceptions.MaquetteRequestException;
import okhttp3.*;

@Getter
@AllArgsConstructor(staticName = "apply")
public final class MaquetteClient {

   private final OkHttpClient client;

   private final ObjectMapper objectMapper;

   private final MaquetteConfiguration config;

   public Request.Builder createRequestFor(String url, Object... args) {
      Request.Builder builder = new Request.Builder()
         .url(String.format("%s%s", config.getUrl(), String.format(url, args)));

      if (config.getAuthentication() instanceof StupidAuthentication) {
         builder = builder
            .addHeader("x-user-id", ((StupidAuthentication) config.getAuthentication()).getUsername())
            .addHeader("x-user-roles", String.join(", ", ((StupidAuthentication) config.getAuthentication()).getRoles()));
      } else if (config.getAuthentication() instanceof ProjectKeyAuthentication) {
         builder = builder
            .addHeader("x-project-key", ((ProjectKeyAuthentication) config.getAuthentication()).getKey())
            .addHeader("x-project-secret", ((ProjectKeyAuthentication) config.getAuthentication()).getSecret());
      } else if (config.getAuthentication() instanceof DataAssetKeyAuthentication) {
         builder = builder
            .addHeader("x-data-asset-key", ((DataAssetKeyAuthentication) config.getAuthentication()).getKey())
            .addHeader("x-data-asset-secret", ((DataAssetKeyAuthentication) config.getAuthentication()).getSecret());
      }

      return builder;
   }

   public void executeCommand(Command command) {
      executeCommand(command, s -> s);
   }

   public <T> T executeCommand(Command command, Function<String, T> mapResponse) {
      var json = Operators.suppressExceptions(() -> objectMapper.writeValueAsString(command));

      var request = createRequestFor("/api/commands")
         .post(RequestBody.create(json, MediaType.parse("application/json; charset=utf-8")))
         .build();

      return executeRequest(
         request,
         res -> mapResponse.apply(Operators.suppressExceptions(res::string)));
   }

   public void executeRequest(Request request) {
      executeRequest(request, s -> s);
   }

   public <T> T executeRequest(Request request, Function<ResponseBody, T> mapResponse) {
      var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

      return Operators.suppressExceptions(() -> {
         ResponseBody body = response.body();

         if (response.isSuccessful() && body != null) {
            return mapResponse.apply(body);
         } else {
            throw MaquetteRequestException.apply(request, response);
         }
      });
   }

}
