package maquette.core.entities.processes;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.values.ActionMetadata;
import maquette.core.entities.processes.model.ProcessDetails;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.values.user.User;
import org.apache.commons.compress.utils.Lists;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ProcessManager {

    private final List<Process> processes;

    public static ProcessManager apply() {
        return apply(Lists.newArrayList());
    }

    synchronized public CompletionStage<Integer> schedule(User user, String description, Function<Process.ProcessLogger, CompletionStage<Done>> runnable) {
        var pid = processes.size() + 1;
        var process = Process.apply(pid, ActionMetadata.apply(user.getDisplayName(), Instant.now()), description, runnable);

        process.run();

        return CompletableFuture.completedFuture(pid);
    }

    public CompletionStage<List<ProcessSummary>> getAll() {
        return Operators.allOf(processes
                .stream()
                .map(Process::getSummary)
                .collect(Collectors.toList()));
    }

    public CompletionStage<Optional<ProcessDetails>> getDetails(int pid) {
        var process =  processes
                .stream()
                .filter(p -> p.getPid() == pid)
                .findAny();

        if (process.isPresent()) {
            return process.get().getDetails().thenApply(Optional::of);
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

}
