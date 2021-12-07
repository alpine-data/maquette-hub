package maquette.development.entities;

import com.google.common.collect.Lists;
import maquette.core.values.UID;
import maquette.development.values.sandboxes.SandboxProperties;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SandboxEntities {

    public CompletionStage<List<SandboxProperties>> listSandboxes(UID id) {
        return CompletableFuture.completedFuture(Lists.newArrayList());
    }

    public SandboxEntity getSandbox(UID id) {
        throw new RuntimeException("not implemented");
    }

}
