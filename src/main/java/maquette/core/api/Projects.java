package maquette.core.api;

import akka.Done;
import maquette.core.values.user.User;

import java.util.concurrent.CompletionStage;

public interface Projects {

    CompletionStage<Integer> create(User user, String name);

    CompletionStage<Done> list(User user);

    CompletionStage<Done> remove(User user, String name);

}
