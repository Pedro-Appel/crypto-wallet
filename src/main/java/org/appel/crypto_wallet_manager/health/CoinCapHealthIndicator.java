package org.appel.crypto_wallet_manager.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component("CoinCap API")
public class CoinCapHealthIndicator implements HealthIndicator {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    private final HttpClient httpClient;
    private final URI baseUri;

    public CoinCapHealthIndicator(@Value("${coincap.api.base-url:https://rest.coincap.io/v3/}") URI baseUri) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        this.baseUri = baseUri;
    }

    @Override
    public Health health() {
        HttpRequest request = HttpRequest.newBuilder(baseUri)
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 500) {
                return Health.up().withDetail("baseUrl", baseUri.toString()).build();
            }
            return Health.down()
                    .withDetail("baseUrl", baseUri.toString())
                    .withDetail("statusCode", response.statusCode())
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withException(ex)
                    .withDetail("baseUrl", baseUri.toString())
                    .build();
        }
    }
}
