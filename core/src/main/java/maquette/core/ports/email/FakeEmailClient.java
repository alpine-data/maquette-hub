package maquette.core.ports.email;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.users.model.UserProfile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class FakeEmailClient implements EmailClient {
    @Override
    public CompletionStage<Done> sendEmail(UserProfile userProfile, String toAddress, String emailBody,
                                           String emailSubject, boolean html, MaquetteRuntime runtime,
                                           String workplace) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
