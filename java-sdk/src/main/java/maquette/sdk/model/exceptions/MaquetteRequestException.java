package maquette.sdk.model.exceptions;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Objects;

public final class MaquetteRequestException extends RuntimeException {

    private MaquetteRequestException(String message) {
        super(message);
    }

    public static MaquetteRequestException apply(Request request, Response response) {
        ResponseBody body = response.body();
        String message;
        String content;

        try {
            if (!Objects.isNull(body)) {
                content = body.string();
            } else {
                content = null;
            }
        } catch (IOException e) {
            content = null;
        }

        if (!Objects.isNull(content)) {
            message = String.format(
                "An exception occurred while executing request %s %s. Response: %s\n\n%s",
                request.method(), request.url().toString(),
                response, content);
        } else {
            message = String.format(
                "An exception occurred while executing request %s %s. Response: %s",
                request.method(), request.url().toString(), response);
        }

        return new MaquetteRequestException(message);
    }

}
