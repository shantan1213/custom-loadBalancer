package com.platformdaemon.customloadbalancer.requestproxy;

import com.platformdaemon.customloadbalancer.Exception.TargetServerDownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;
import tools.jackson.databind.JsonNode;

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
        } catch (ResourceAccessException e) {
            throw new TargetServerDownException(
                    "Target server is unavailable: " + BackendServerConstant.BACKEND_ONE, e
            );
        }
    }

    public ResponseEntity<byte[]> forwardCreate(JsonNode requestBody) {
        return forwardJson(HttpMethod.POST, "/create", requestBody);
    }

    public ResponseEntity<byte[]> forwardUpdate(Long id, JsonNode requestBody) {
        return forwardJson(HttpMethod.PUT, "/update/" + id, requestBody);
    }

    private ResponseEntity<byte[]> forwardJson(HttpMethod method, String path, JsonNode requestBody) {
        try {
            return restClient.method(method)
                    .uri(BackendServerConstant.BACKEND_ONE + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .exchange((request, response) -> {
                        HttpHeaders headers = new HttpHeaders();
                        headers.putAll(response.getHeaders());
                        return new ResponseEntity<>(
                                response.getBody().readAllBytes(),
                                headers,
                                response.getStatusCode()
                        );
                    });
        } catch (ResourceAccessException e) {
            throw new TargetServerDownException(
                    "Target server is unavailable: " + BackendServerConstant.BACKEND_ONE, e
            );
        }
    }

}
