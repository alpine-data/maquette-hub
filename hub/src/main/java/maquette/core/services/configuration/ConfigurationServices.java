package maquette.core.services.configuration;

import java.util.concurrent.CompletionStage;

public interface ConfigurationServices {

   CompletionStage<String> getDefaultDataOwner();

}
