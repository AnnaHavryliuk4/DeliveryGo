package org.geekhub.coursework.delivery.authorization.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class UtilsToken {
    private static final long EXPIRE_TOKEN = 30;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN;
    }
}
