package com.skushwaha.getform.Exception;

public class OtpRateLimitException
        extends RuntimeException {

    public OtpRateLimitException(String message) {
        super(message);
    }
}
