package maquette.core.services;

import maquette.core.entities.sandboxes.model.SandboxDetails;
import maquette.core.entities.sandboxes.model.SandboxProperties;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SandboxServices {

   CompletionStage<SandboxDetails> createSandbox(User user, String project, String name);

   CompletionStage<SandboxDetails> getSandbox(User user, String project, String sandbox);

   CompletionStage<List<SandboxProperties>> listSandboxes(User user, String project);

}
