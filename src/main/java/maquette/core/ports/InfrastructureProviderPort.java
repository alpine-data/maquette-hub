package maquette.core.ports;

import akka.Done;

import java.util.concurrent.CompletionStage;

public interface InfrastructureProviderPort {

    CompletionStage<Container> createContainer(String image);

    interface Container {

        CompletionStage<Done> stop();

    }

}
