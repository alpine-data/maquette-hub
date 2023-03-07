package maquette.development.values.exceptions;

import maquette.core.common.exceptions.ApplicationException;
import maquette.core.values.UID;

public final class WorkspaceNotFoundException extends ApplicationException {

    private WorkspaceNotFoundException(String message) {
        super(message);
    }

    public static WorkspaceNotFoundException applyFromName(String name) {
        String msg = String.format("Workspace with name `%s` was not found.", name);
        return new WorkspaceNotFoundException(msg);
    }

    public static WorkspaceNotFoundException applyFromId(UID id) {
        String msg = String.format("Workspace with id `%s` was not found.", id);
        return new WorkspaceNotFoundException(msg);
    }

    public static WorkspaceNotFoundException applyFromMLflowId(String mlflowId) {
        String msg = String.format("Workspace noz found for MLflow id `%s`.", mlflowId);
        return new WorkspaceNotFoundException(msg);
    }

    @Override
    public int getHttpStatus() {
        return 404;
    }
}
