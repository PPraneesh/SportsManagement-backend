package com.praneesh.sports.team_service.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class InvitationTokenUtil {

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private InvitationTokenUtil() {
    }

    public static String generateToken() {

        byte[] bytes = new byte[32];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}