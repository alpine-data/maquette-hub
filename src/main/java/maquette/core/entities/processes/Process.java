package maquette.core.entities.processes;

import akka.Done;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.values.ActionMetadata;
import maquette.core.entities.processes.model.ProcessDetails;
import maquette.core.entities.processes.model.ProcessStatus;
import maquette.core.entities.processes.model.ProcessSummary;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class Process {

    private final int pid;

    private final String description;

    private final Function<ProcessLogger, CompletionStage<Done>> runnable;

    private final ProcessLogger logs;

    private ProcessStatus status;

    static Process apply(
            int pid, ActionMetadata initiated, String description,
            Function<ProcessLogger, CompletionStage<Done>> run) {

        var log = LoggerFactory.getLogger(Process.class);
        var logs = ProcessLogger.apply(pid, log, new ArrayList<>());

        log.debug("Initializing Process `{}` with PID {}", description, pid);

        return apply(pid, description, run, logs, ProcessStatus.Scheduled.apply(initiated));
    }


    public int getPid() {
        return pid;
    }

    public String getDescription() {
        return description;
    }

    public CompletionStage<List<String>> getLogs() {
        return CompletableFuture.completedFuture(List.copyOf(logs.logs));
    }

    public CompletionStage<ProcessStatus> getStatus() {
        return CompletableFuture.completedFuture(status);
    }

    public CompletionStage<ProcessSummary> getSummary() {
        var summary = ProcessSummary.apply(pid, status.getCreated(), description, status.toString());
        return CompletableFuture.completedFuture(summary);
    }

    public CompletionStage<ProcessDetails> getDetails() {
        var details = ProcessDetails.apply(pid, description, status, logs.logs);
        return CompletableFuture.completedFuture(details);
    }

    public CompletionStage<Done> run() {
        if (status instanceof ProcessStatus.Scheduled) {
            var created = status.getCreated();
            var started = Instant.now();

            status = ProcessStatus.Running.apply(created, started);

            return runnable
                    .apply(logs)
                    .thenApply(done -> {
                        var finished = Instant.now();
                        status = ProcessStatus.Success.apply(created, started, finished);
                        return Done.getInstance();
                    })
                    .exceptionally(ex -> {
                        var failed = Instant.now();
                        status = ProcessStatus.Failed.apply(created, started, failed, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
                        return Done.getInstance();
                    });
        } else {
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }

    @AllArgsConstructor(staticName = "apply")
    public static class ProcessLogger {

        private final int pid;

        private final Logger log;

        private final List<String> logs;

        private void log(String message, Object ...args) {
            logs.add(String.format(message, args));
        }

        public void debug(String message, Object ...args) {
            if (log.isDebugEnabled()) {
                log("[DEBUG] %s", message, args);
            }

            log.debug("[PID: {}] {}", pid, String.format(message, args));
        }

        public void error(String message, Throwable throwable, Object ...args) {
            if (log.isErrorEnabled()) {
                var trace = ExceptionUtils.getFullStackTrace(throwable).replaceAll("(?m)^", ">   ");
                log("[ERROR] %s\n%s", String.format(message, args), trace);
            }

            log.error(String.format("[PID: %d] %s", pid, String.format(message, args)), throwable);
        }

        public void info(String message, Object ...args) {
            if (log.isInfoEnabled()) {
                log("[INFO] %s", message, args);
            }

            log.info("[PID: {}] {}", pid, String.format(message, args));
        }

        public void warn(String message, Throwable throwable, Object ...args) {
            if (log.isWarnEnabled()) {
                var trace = ExceptionUtils.getFullStackTrace(throwable).replaceAll("(?m)^", ">   ");
                log("[WARN] %s\n%s", String.format(message, args), trace);
            }

            log.warn(String.format("[PID: %d] %s", pid, String.format(message, args)), throwable);
        }

    }

}
