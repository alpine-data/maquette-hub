package maquette.core.ports;

import akka.Done;

import java.util.concurrent.CompletionStage;

public interface MlflowProxyPort {

   CompletionStage<Done> registerRoute(String id, String route, String target);

   CompletionStage<Done> removeRoute(String id);

}
