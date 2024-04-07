package com.razzzil.telegram.exception;

public class BotInitializingException extends Exception {
    public BotInitializingException() {
    }

    public BotInitializingException(String message) {
        super(message);
    }

    public BotInitializingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BotInitializingException(Throwable cause) {
        super(cause);
    }

    public BotInitializingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
