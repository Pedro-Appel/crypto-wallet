package org.appel.crypto_wallet_manager.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coinCapClient", url = "${coincap.api.base-url}")
public interface CoinCapClient {

    @GetMapping("/v3/assets")
    @Retry(name = "coinCap")
    @CircuitBreaker(name = "coinCap")
    AssetPriceResponse fetchAssetPrices(@RequestParam("ids") String ids,
                                        @RequestParam("apiKey") String apiKey);

    @GetMapping("/v3/assets")
    @Retry(name = "coinCap")
    @CircuitBreaker(name = "coinCap")
    AssetPriceResponse searchAssetName(@RequestParam("search") String symbol,
                                      @RequestParam(value = "limit", defaultValue = "1") int limit,
                                      @RequestParam(value = "apiKey") String apiKey);
}