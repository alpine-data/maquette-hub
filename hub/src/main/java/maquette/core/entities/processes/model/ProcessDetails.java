package maquette.core.entities.processes.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class ProcessDetails {

    int pid;

    String description;

    ProcessStatus status;

    String log;

    public static ProcessDetails apply(int pid, String description, ProcessStatus status, List<String> logs) {
        var log = String.join("\n", logs);
        return apply(pid, description, status, log);
    }

}
