package com.skushwaha.getform.Email.Util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOtp() {

        return String.valueOf(
                100000 + RANDOM.nextInt(900000)
        );
    }
}
