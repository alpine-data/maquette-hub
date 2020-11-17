package maquette.core.entities.infrastructure;

import akka.Done;
import maquette.core.entities.infrastructure.model.ContainerConfig;
import maquette.core.entities.infrastructure.model.ContainerProperties;
import maquette.core.entities.infrastructure.model.ContainerStatus;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface Container {

    ContainerConfig getConfig();

    CompletionStage<ContainerStatus> getStatus();

    CompletionStage<Map<Integer, URL>> getMappedPortUrls();

    CompletionStage<String> getLogs();

    CompletionStage<Done> start();

    CompletionStage<Done> stop();

    CompletionStage<Done> remove();

    CompletionStage<ContainerProperties> getProperties();

}
