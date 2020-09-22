package maquette.core.services;

import maquette.core.entities.processes.model.ProcessDetails;
import maquette.core.entities.processes.model.ProcessSummary;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ProcessServices {

    CompletionStage<List<ProcessSummary>> getAll(User user);

    CompletionStage<Optional<ProcessDetails>> getDetails(int pid);

}
