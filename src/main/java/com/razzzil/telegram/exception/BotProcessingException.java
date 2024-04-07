package com.razzzil.telegram.exception;

public class BotProcessingException extends RuntimeException {
    public BotProcessingException() {
    }

    public BotProcessingException(String message) {
        super(message);
    }

    public BotProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BotProcessingException(Throwable cause) {
        super(cause);
    }

    public BotProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
