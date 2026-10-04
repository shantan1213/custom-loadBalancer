package com.platformdaemon.customloadbalancer.requestproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class ProxyService {

    @Autowired
    RequestForwarder requestForwarder;

    
    public String forwardPingToRf(){
        ResponseEntity<String> response = requestForwarder.forwardPing();
        return response.getBody();
    }

    public ResponseEntity<byte[]> forwardCreateToRf(JsonNode requestBody) {
        return requestForwarder.forwardCreate(requestBody);
    }

    public ResponseEntity<byte[]> forwardUpdateToRf(Long id, JsonNode requestBody) {
        return requestForwarder.forwardUpdate(id, requestBody);
    }
}
