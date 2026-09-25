package com.praneesh.sports.team_service.util;

public final class InvitationUtil {

    private InvitationUtil() {
    }

    public static String buildInvitationUrl(
            String frontendBaseUrl,
            String token
    ) {

        String base =
                frontendBaseUrl.endsWith("/")
                        ? frontendBaseUrl.substring(
                        0,
                        frontendBaseUrl.length() - 1
                )
                        : frontendBaseUrl;

        return base + "/invitations/" + token;
    }
}