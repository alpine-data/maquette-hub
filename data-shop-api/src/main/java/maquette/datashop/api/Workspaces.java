package maquette.datashop.api;

import java.util.concurrent.CompletionStage;

public interface Workspaces {

   CompletionStage<Workspace> getWorkspaceByName(String name);

}
