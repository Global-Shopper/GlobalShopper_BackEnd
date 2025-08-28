package com.sep490.gshop.service.implement;

import com.google.firebase.messaging.*;
import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.FCMToken;
import com.sep490.gshop.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SendNotiService {


    private final UserBusiness userBusiness;

    @Autowired
    public SendNotiService(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public String sendNotificationToToken(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK") // nếu mobile dùng Flutter/React Native
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    public BatchResponse sendNotificationToTokens(List<String> tokens, String title, String body) throws FirebaseMessagingException {
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        return FirebaseMessaging.getInstance().sendEachForMulticast(message);
    }

    @Async
    public CompletableFuture<Boolean> sendNotiToUser(UUID id, String title, String body) {
        try {
            User user = userBusiness.getById(id).orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));
            if (user.getTokens() == null || user.getTokens().isEmpty()) {
                return CompletableFuture.completedFuture(true);
            }
            List<String> tokens = user.getTokens().stream().filter(token -> token != null && token.getIsActive()).map(FCMToken::getToken).toList();
            sendNotificationToTokens(tokens, title, body);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", id, e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }
}
