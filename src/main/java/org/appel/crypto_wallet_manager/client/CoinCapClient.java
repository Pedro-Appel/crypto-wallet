package org.appel.crypto_wallet_manager.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coinCapClient", url = "${coincap.api.base-url}")
public interface CoinCapClient {

    @Retry(name = "coinCap")
    @CircuitBreaker(name = "coinCap")
    @GetMapping(value = "/v3/assets",
            params = {
                    "apiKey=${coincap.api.apiKey}"
            })
    AssetPriceResponse fetchAssetPrices(@RequestParam("ids") String ids);

    @Retry(name = "coinCap")
    @CircuitBreaker(name = "coinCap")
    @GetMapping(value = "/v3/assets",
            params = {
                    "apiKey=${coincap.api.apiKey}",
                    "limit=1"
            })
    AssetPriceResponse searchAssetName(@RequestParam("search") String symbol);

    @CircuitBreaker(name = "coinCap")
    @GetMapping(value = "/v3/assets/{slug}/history",
            params = {
                    "apiKey=${coincap.api.apiKey}",
                    "interval=d1"
            })
    AssetPriceResponse fetchHistoricalPrice(@PathVariable(value = "slug") String symbol,
                                            @RequestParam(value = "start") String startDate,
                                            @RequestParam(value = "end") String fromDate);
}