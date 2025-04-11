package com.example.enterpriseapi.client;

import com.example.enterpriseapi.config.SageworksConfig;
import com.example.enterpriseapi.dto.auth.TokenResponse;
import com.example.enterpriseapi.util.CorrelationIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SageworksClient {
    private static final Logger logger = LoggerFactory.getLogger(SageworksClient.class);
    private static final long TOKEN_TTL_MS = 900_000; // 900 seconds in milliseconds
    private final RestTemplate restTemplate = new RestTemplate();
    private final SageworksConfig config;
    
    private final AtomicReference<TokenCache> tokenCache = new AtomicReference<>();

    public SageworksClient(SageworksConfig config) {
        this.config = config;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-correlation-id", CorrelationIdUtils.getCurrentCorrelationId());
        return headers;
    }

    public String getAccessToken() {
        TokenCache currentCache = tokenCache.get();
        long currentTime = System.currentTimeMillis();

        if (currentCache != null && (currentTime - currentCache.timestamp) < TOKEN_TTL_MS) {
            logger.debug("Using cached access token");
            return currentCache.token;
        }

        logger.info("Requesting new access token from Sageworks");
        String newToken = fetchNewAccessToken();
        tokenCache.set(new TokenCache(newToken, currentTime));
        return newToken;
    }

    private String fetchNewAccessToken() {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String auth = config.getClientId() + ":" + config.getClientSecret();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + encodedAuth);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            logger.debug("Attempting to connect to Sageworks auth URL: {}", config.getAuthUrl());
            
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                config.getAuthUrl(),
                request,
                TokenResponse.class
            );
            
            logger.info("Successfully retrieved new access token from Sageworks");
            return response.getBody().getAccessToken();
        } catch (ResourceAccessException e) {
            logger.error("Failed to connect to Sageworks auth server. URL: {}, Error: {}", 
                config.getAuthUrl(), e.getMessage());
            throw new RuntimeException("Unable to connect to Sageworks authentication server. " +
                "Please check the URL and your network connection.", e);
        } catch (RestClientException e) {
            logger.error("Failed to authenticate with Sageworks. Error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed with Sageworks server. " +
                "Please check your credentials.", e);
        }
    }

    private static class TokenCache {
        final String token;
        final long timestamp;

        TokenCache(String token, long timestamp) {
            this.token = token;
            this.timestamp = timestamp;
        }
    }

    public String getProposedLoanById(String id, String accessToken) {
        logger.info("Fetching proposed loan data from Sageworks for ID: {}", id);
        HttpHeaders headers = createHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                config.getApiUrl() + "/" + id,
                HttpMethod.GET,
                request,
                String.class
        );

        logger.info("Successfully retrieved proposed loan data from Sageworks for ID: {}", id);
        return response.getBody();
    }
}