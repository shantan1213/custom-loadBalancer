package com.platformdaemon.customloadbalancer.requestproxy;

import com.platformdaemon.customloadbalancer.Exception.TargetServerDownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RequestForwarder {
    @Autowired
    BackendServerConstant backendServerConstant;

    private final RestClient restClient;

    public RequestForwarder() {
        this.restClient = RestClient.builder().build();
    }
    public ResponseEntity<String> forwardPing() {
        try {
            return restClient.get()
                    .uri(BackendServerConstant.BACKEND_ONE + "/ping")
                    .retrieve()
                    .toEntity(String.class);
        } catch (Exception e) {
            throw new TargetServerDownException(
                    "Target server is unavailable: "+ BackendServerConstant.BACKEND_ONE
            );
        }
    }

}
