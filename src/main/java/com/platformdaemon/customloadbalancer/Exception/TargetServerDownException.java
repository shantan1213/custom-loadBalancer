package com.platformdaemon.customloadbalancer.Exception;

public class TargetServerDownException extends RuntimeException{
    public TargetServerDownException(String message, Throwable cause) {
        super(message, cause);
    }
}
