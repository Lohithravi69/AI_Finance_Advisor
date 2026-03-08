package com.aifa.finance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@SuppressWarnings("null")
public class KeycloakTokenService {

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth.keycloak.token-url}")
    private String tokenUrl;

    @Value("${auth.keycloak.client-id:aifa-web}")
    private String defaultClientId;

    @Value("${auth.keycloak.client-secret:}")
    private String clientSecret;

    public ResponseEntity<Map<String, Object>> exchangeToken(MultiValueMap<String, String> incomingForm) {
        String grantType = incomingForm.getFirst("grant_type");
        validateGrantRequest(grantType, incomingForm);

        MultiValueMap<String, String> outboundForm = new LinkedMultiValueMap<>();
        outboundForm.add("grant_type", grantType);

        String clientId = incomingForm.getFirst("client_id");
        outboundForm.add("client_id", (clientId == null || clientId.isBlank()) ? defaultClientId : clientId);

        if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
            outboundForm.add("code", incomingForm.getFirst("code"));
            String redirectUri = incomingForm.getFirst("redirect_uri");
            if (redirectUri != null && !redirectUri.isBlank()) {
                outboundForm.add("redirect_uri", redirectUri);
            }
            copyIfPresent(incomingForm, outboundForm, "code_verifier");
        }

        if (GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
            outboundForm.add("refresh_token", incomingForm.getFirst("refresh_token"));
        }

        copyIfPresent(incomingForm, outboundForm, "scope");

        if (clientSecret != null && !clientSecret.isBlank()) {
            outboundForm.add("client_secret", clientSecret);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(outboundForm, headers);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>)
                    restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);
            Map<String, Object> body = response.getBody();
            return ResponseEntity.status(response.getStatusCode()).body(body == null ? Map.of() : body);
        } catch (HttpStatusCodeException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            Map<String, Object> errorBody = Map.of(
                    "error", "token_exchange_failed",
                    "message", ex.getResponseBodyAsString()
            );
            return ResponseEntity.status(statusCode).body(errorBody);
        }
    }

    private void validateGrantRequest(String grantType, MultiValueMap<String, String> form) {
        if (!GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType) && !GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
            throw new IllegalArgumentException("Unsupported grant_type");
        }

        if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
            String code = form.getFirst("code");
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("Missing required parameter: code");
            }
        }

        if (GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
            String refreshToken = form.getFirst("refresh_token");
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("Missing required parameter: refresh_token");
            }
        }
    }

    private void copyIfPresent(MultiValueMap<String, String> source, MultiValueMap<String, String> target, String key) {
        String value = source.getFirst(key);
        if (value != null && !value.isBlank()) {
            target.add(key, value);
        }
    }
}