package com.platformdaemon.customloadbalancer.requestproxy;

import org.springframework.stereotype.Component;

@Component
public class BackendServerConstant {
    public static final String BACKEND_ONE = "http://localhost:11111";
    public static final String BACKEND_TWO = "http://localhost:22222";
}
