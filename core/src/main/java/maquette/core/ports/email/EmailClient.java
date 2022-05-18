package maquette.core.ports.email;

import akka.Done;
import maquette.core.modules.users.model.UserProfile;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public interface EmailClient {
    CompletionStage<Done> sendEmail(UserProfile userProfile, String toAddress, String emailBody, String emailSubject, boolean html);
}
