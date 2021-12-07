package maquette.testutils;

import akka.actor.ActorSystem;
import io.javalin.Javalin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import scala.jdk.javaapi.FutureConverters;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaquetteContext {

    public final ActorSystem system;

    public final Javalin app;

    public final Authorizations authorizations;

    public final Users users;

    public static MaquetteContext apply() {
        var system = ActorSystem.create("maquette-test");
        var app = Javalin.create();

        return new MaquetteContext(system, app, Authorizations.apply(), Users.apply());
    }

    public void clean() {
        Operators.suppressExceptions(() -> {
            FutureConverters.asJava(this.system.terminate()).toCompletableFuture().get();
        });
    }

}
