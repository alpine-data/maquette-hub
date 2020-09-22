package maquette.core.entities.processes.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.time.Instant;

public interface ProcessStatus {

    ActionMetadata getCreated();

    @Value
    @AllArgsConstructor(staticName = "apply")
    class Scheduled implements ProcessStatus {

        ActionMetadata created;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    class Running implements ProcessStatus {

        ActionMetadata created;

        Instant since;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    class Success implements ProcessStatus {

        ActionMetadata created;

        Instant started;

        Instant finished;

    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    class Failed implements ProcessStatus {

        ActionMetadata created;

        Instant started;

        Instant failed;

        String message;

        String stackTrace;

    }

}
