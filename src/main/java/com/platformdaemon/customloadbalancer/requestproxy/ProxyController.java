package com.platformdaemon.customloadbalancer.requestproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    @GetMapping("/ping")
    public String forwardPing(){
        return proxyService.forwardPingToRf();
    }

    @PostMapping("/create")
    public ResponseEntity<byte[]> createUser(@RequestBody JsonNode requestBody) {
        return proxyService.forwardCreateToRf(requestBody);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<byte[]> updateUser(@PathVariable Long id, @RequestBody JsonNode requestBody) {
        return proxyService.forwardUpdateToRf(id, requestBody);
    }
}
