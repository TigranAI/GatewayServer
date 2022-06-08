package ru.tigran.gatewayproxy.util;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthorizeRequest {
    private final RestTemplate restTemplate;

    private final String LOGIN_URL = "http://localhost/authorize/login";
    private final String REGISTER_URL = "http://localhost/authorize/register";
    private final String REFRESH_URL = "http://localhost/authorize/refresh";

    public AuthorizeRequest(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<String> doLogin(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, getHeaders());

        return this.restTemplate.postForEntity(LOGIN_URL, entity, String.class);
    }

    public ResponseEntity<String> doRegister(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, getHeaders());

        return this.restTemplate.postForEntity(REFRESH_URL, entity, String.class);
    }

    public ResponseEntity<String> doRefresh(String jrt) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("jrt", jrt);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, getHeaders());

        return this.restTemplate.postForEntity(REGISTER_URL, entity, String.class);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
