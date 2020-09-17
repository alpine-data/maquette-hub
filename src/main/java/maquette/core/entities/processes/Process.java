package maquette.core.entities.processes;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.concurrent.CompletedFuture;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class Process {

    private final int pid;

    private final ActionMetadata initiated;

    private final String description;

    private List<String> log;

    private ProcessStatus status;

    private

    Runnable runnable;

    public int getPid() {
        return pid;
    }

    public String getDescription() {
        return description;
    }

    public CompletionStage<List<String>> getLog() {
        return CompletableFuture.completedFuture(log);
    }

    public CompletionStage<ProcessStatus> getStatus() {
        return CompletableFuture.completedFuture(status);
    }

}
