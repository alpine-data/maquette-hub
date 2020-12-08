package maquette.core.services.data;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.access.DataAccessRequestProperties;
import maquette.core.values.user.User;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

public interface AccessRequestServices {

   CompletionStage<DataAccessRequestProperties> createDataAccessRequest(User executor, String asset, String project, String reason);

   CompletionStage<DataAccessRequest> getDataAccessRequest(User executor, String asset, UID request);

   CompletionStage<Done> grantDataAccessRequest(User executor, String asset, UID request, @Nullable Instant until, @Nullable String message);

   CompletionStage<Done> rejectDataAccessRequest(User executor, String asset, UID request, String reason);

   CompletionStage<Done> updateDataAccessRequest(User executor, String asset, UID request, String reason);

   CompletionStage<Done> withdrawDataAccessRequest(User executor, String asset, UID request, @Nullable String reason);

}
