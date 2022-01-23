package maquette.development.ports.infrastructure.docker.deployments;

import java.util.ArrayList;
import java.util.List;

public final class StackDeploymentList extends ArrayList<StackDeployment> {

    public static StackDeploymentList apply(List<StackDeployment> actual) {
        var l = new StackDeploymentList();
        l.addAll(actual);
        return l;
    }

}
