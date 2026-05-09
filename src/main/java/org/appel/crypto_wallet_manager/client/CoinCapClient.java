package org.appel.crypto_wallet_manager.client;

import org.appel.crypto_wallet_manager.dto.AssetPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coinCapClient", url = "${coincap.api.base-url}")
public interface CoinCapClient {

    @GetMapping("/v3/assets")
    AssetPriceResponse fetchAssetPrices(@RequestParam("ids") String ids,
                                        @RequestParam("apiKey") String apiKey);

    @GetMapping("/v3/assets")
    AssetPriceResponse searchAssetName(@RequestParam("search") String symbol,
                                      @RequestParam(value = "limit", defaultValue = "1") int limit,
                                      @RequestParam(value = "apiKey") String apiKey);
}