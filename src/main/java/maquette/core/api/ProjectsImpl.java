package maquette.core.api;

import akka.Done;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.concurrent.CompletedFuture;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public class ProjectsImpl implements Projects {

    @Override
    public CompletionStage<Integer> create(User user, String name) {
        return ;
    }

    @Override
    public CompletionStage<Done> list(User user) {
        return null;
    }

    @Override
    public CompletionStage<Done> remove(User user, String name) {
        return null;
    }

}
