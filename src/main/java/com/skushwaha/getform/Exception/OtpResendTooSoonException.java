package com.skushwaha.getform.Exception;

public class OtpResendTooSoonException
        extends RuntimeException {

    public OtpResendTooSoonException(String message) {
        super(message);
    }
}