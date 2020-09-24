package maquette.core.entities.infrastructure;

import akka.Done;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.ContainerStatus;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface Container {

    ContainerConfig getConfig();

    ContainerStatus getStatus();

    Map<Integer, URL> getMappedPortUrls();

    String getLogs();

    CompletionStage<Done> start();

    CompletionStage<Done> stop();

    CompletionStage<Done> remove();

}
