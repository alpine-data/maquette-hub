package maquette.sdk.dsl;

import akka.Done;
import akka.japi.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.common.ObjectMapperFactory;
import maquette.common.Operators;
import maquette.sdk.commands.Command;
import maquette.sdk.model.exceptions.MaquetteRequestException;
import okhttp3.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Value
@With
@AllArgsConstructor(staticName = "apply")
public class MaquetteConfiguration {

    String baseUrl;

    String user;

    String token;

    ObjectMapper om;

    OkHttpClient client;

    public static MaquetteConfiguration apply() {
        return apply("http://localhost:9042", "alice", null, ObjectMapperFactory.apply().createJson(true), new OkHttpClient());
    }

    public Request.Builder createRequestFor(String url, Object ...args) {
        Request.Builder builder = new Request.Builder()
            .url(String.format("%s%s", baseUrl, String.format(url, args)))
            .header("x-user-id", user);

        if (token != null) {
            builder = builder.header("x-user-token", token);
        }

        return builder;
    }

    public CompletionStage<Done> executeCommand(Command command) {
        return executeCommand(command, s -> CompletableFuture.completedFuture(Done.getInstance()));
    }

    public <T> T executeCommand(Command command, Function<String, T> mapResponse) {
        var json = Operators.suppressExceptions(() -> om.writeValueAsString(command));

        var request = createRequestFor("/api/commands")
           .post(RequestBody.create(json, MediaType.parse("application/json; charset=utf-8")))
           .build();

        var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

        return Operators.suppressExceptions(() -> {
            ResponseBody body = response.body();

            if (response.isSuccessful() && body != null) {
                var jsonResponse = body.string();
                return mapResponse.apply(jsonResponse);
            } else {
                throw MaquetteRequestException.apply(request, response);
            }
        });
    }

}
