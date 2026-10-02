package com.platformdaemon.customloadbalancer.Exception;

public class TargetServerDownException extends RuntimeException{
    public TargetServerDownException(String message) {
        super(message);
    }
}
