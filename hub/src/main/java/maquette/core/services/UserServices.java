package maquette.core.services;

import akka.Done;
import maquette.core.entities.users.model.UserNotification;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface UserServices {

   CompletionStage<List<UserNotification>> getNotifications(User executor);

   CompletionStage<Done> readNotification(User executor, String notificationId);

}
