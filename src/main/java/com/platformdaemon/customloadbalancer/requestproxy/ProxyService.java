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

    public ResponseEntity<JsonNode> forwardCreateToRf(JsonNode requestBody) {
        ResponseEntity<JsonNode> response = requestForwarder.forwardCreate(requestBody);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
