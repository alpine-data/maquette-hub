package maquette.adapters.infrastructure;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.ports.MlflowProxyPort;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteMlflowProxyService implements MlflowProxyPort {

   private static final Logger LOG = LoggerFactory.getLogger(MaquetteMlflowProxyService.class);

   ObjectMapper om;

   OkHttpClient client;

   String serviceUrl;

   public static MaquetteMlflowProxyService apply(ObjectMapper om) {
      OkHttpClient client = new OkHttpClient.Builder()
         .readTimeout(3, TimeUnit.MINUTES)
         .build();
      return apply(om, client, "http://127.0.0.1:3040");
   }

   @Override
   public CompletionStage<Done> registerRoute(String id, String route, String target) {
      return CompletableFuture.supplyAsync(() -> {
         var json = Operators.suppressExceptions(() ->
            om.writeValueAsString(RegisterRequest.apply(id, route, target)));

         var request = new Request.Builder()
            .url(String.format("%s/api/routes", serviceUrl))
            .post(RequestBody.create(json, MediaType.parse("application/json")))
            .build();

         try {
            var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

            if (!response.isSuccessful()) {
               LOG.error(
                  "Unable to register route `{}`, `{}`, `{}` at endpoint `{}`. Received error from server:\n{}",
                  id, route, target, serviceUrl,
                  Operators.ignoreExceptionsWithDefault(() -> {
                     var body = response.body();

                     if (body != null) {
                        return body.string();
                     } else {
                        return "";
                     }
                  }, ""));
            }
         } catch (Exception e) {
            LOG.warn("Exception occurred while registering route `{}`, `{}`, `{}` at endpoint `{}`.", id, route, target, serviceUrl, e);
         }

         return Done.getInstance();
      });
   }

   @Override
   public CompletionStage<Done> removeRoute(String id) {
      return CompletableFuture.supplyAsync(() -> {
         var request = new Request.Builder()
            .url(String.format("%s/api/routes/%s", serviceUrl, id))
            .delete()
            .build();

         try {
            var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

            if (!response.isSuccessful()) {
               LOG.error(
                  "Unable to remove route `{}` at endpoint `{}`. Received error from server:\n{}",
                  id, serviceUrl,
                  Operators.ignoreExceptionsWithDefault(() -> {
                     var body = response.body();

                     if (body != null) {
                        return body.string();
                     } else {
                        return "";
                     }
                  }, ""));
            }
         } catch (Exception e) {
            LOG.warn("Exception occurred while removing route `{}` at endpoint `{}`.", id, serviceUrl, e);
         }

         return Done.getInstance();
      });
   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class RegisterRequest {

      String id;

      String route;

      String target;

   }

}
