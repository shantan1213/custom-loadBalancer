package com.platformdaemon.customloadbalancer.requestproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProxyService {

    @Autowired
    RequestForwarder requestForwarder;

    
    public String forwardPingToRf(){
        ResponseEntity<String> response = requestForwarder.forwardPing();
        return response.getBody();
    }
}
