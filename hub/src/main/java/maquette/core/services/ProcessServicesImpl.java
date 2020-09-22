package maquette.core.services;

import lombok.AllArgsConstructor;
import maquette.core.entities.processes.ProcessManager;
import maquette.core.entities.processes.model.ProcessDetails;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class ProcessServicesImpl implements ProcessServices {

    private final ProcessManager processManager;

    @Override
    public CompletionStage<List<ProcessSummary>> getAll(User user) {
        return processManager.getAll();
    }

    @Override
    public CompletionStage<Optional<ProcessDetails>> getDetails(int pid) {
        return processManager.getDetails(pid);
    }

}
