package com.platformdaemon.customloadbalancer.requestproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    @GetMapping("/ping")
    public String forwardPing(){
        return proxyService.forwardPingToRf();
    }
}
