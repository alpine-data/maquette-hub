package maquette.adapters;

import akka.Done;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.ResponseItem;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class CompletionStageResultCallback<T extends ResponseItem> implements ResultCallback<T> {

    private final String executedCommand;

    private final Logger log;

    private final CompletableFuture<Done> result;

    public static <T extends ResponseItem> CompletionStageResultCallback<T> apply(String executedCommand, Logger log) {
        return apply(executedCommand, log, new CompletableFuture<>());
    }

    @Override
    public void onStart(Closeable closeable) {

    }

    @Override
    public void onNext(T object) {
        if (object.isErrorIndicated()) {
            log.warn("`{}` - {}", executedCommand, object.getErrorDetail() != null ? object.getErrorDetail().getMessage() : object.getStatus());
        } else {
            log.info("`{}` - {}", executedCommand, object.getStatus());
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(String.format("`%s` - Error", executedCommand), throwable);
        result.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        log.info("`{}` - Completed", executedCommand);
        result.complete(Done.getInstance());
    }

    @Override
    public void close() {

    }

    public CompletionStage<Done> result() {
        return result;
    }

}
