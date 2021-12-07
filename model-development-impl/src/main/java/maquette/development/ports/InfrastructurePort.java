package maquette.development.ports;

import akka.Done;
import maquette.core.values.UID;
import maquette.development.values.stacks.StackConfiguration;
import maquette.development.values.stacks.StackInstanceParameters;
import maquette.development.values.stacks.StackInstanceStatus;

import java.util.concurrent.CompletionStage;

public interface InfrastructurePort {

    CompletionStage<Done> createOrUpdateStackInstance(UID workspace,
                                                      StackConfiguration configuration);

    CompletionStage<Done> removeStackInstance(String name);

    CompletionStage<StackInstanceParameters> getInstanceParameters(UID workspace,
                                                                   String name);

    CompletionStage<StackInstanceStatus> getStackInstanceStatus(String name);

}
